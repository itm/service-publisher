package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.servlet.Filter;
import javax.ws.rs.core.Application;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;

public abstract class ServicePublisherBase extends AbstractService implements ServicePublisher {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	protected final List<Service> servicesPublished = newArrayList();

	protected final List<Service> servicesUnpublished = newArrayList();

	protected final ServicePublisherConfig config;

	protected ServicePublisherBase(final ServicePublisherConfig config) {
		this.config = config;
	}

	@Override
	protected void doStart() {
		try {
			doStartInternal();
			for (Service service : newArrayList(servicesUnpublished)) {
				service.startAndWait();
			}
			log.info("Started server on port {}", config.getPort());
			notifyStarted();
		} catch (Exception e) {
			log.error("Failed to start server on port " + config.getPort() + " due to the following error: ", e);
			notifyFailed(e);
		}
	}

	protected abstract void doStartInternal() throws Exception;

	@Override
	protected void doStop() {
		log.info("Stopping server on port {}", config.getPort());
		try {
			doStopInternal();
			notifyStopped();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	protected abstract void doStopInternal() throws Exception;

	@Override
	public ServicePublisherService createServletService(final String contextPath, final String resourceBase) {
		return createServletService(contextPath, resourceBase, null);
	}

	@Override
	public ServicePublisherService createServletService(final String contextPath, final String resourceBase,
														@Nullable final Map<String, String> initParams,
														final Filter... filters) {
		final ServicePublisherService service = createServletServiceInternal(contextPath, resourceBase, initParams, filters);
		servicesUnpublished.add(service);
		service.addListener(createServiceListener(service), sameThreadExecutor());
		return service;
	}

	protected abstract ServicePublisherService createServletServiceInternal(String contextPath, String resourceBase,
																			final Map<String, String> initParams,
																			final Filter... filters);

	@Override
	public ServicePublisherService createJaxRsService(final String contextPath, final Application application) {
		final ServicePublisherService service = createJaxRsServiceInternal(contextPath, application);
		servicesUnpublished.add(service);
		service.addListener(createServiceListener(service), sameThreadExecutor());
		return service;
	}

	protected abstract ServicePublisherService createJaxRsServiceInternal(final String contextPath, final Application application);

	@Override
	public ServicePublisherService createJaxWsService(final String contextPath, final Object endpointImpl) {
		final ServicePublisherService service = createJaxWsServiceInternal(contextPath, endpointImpl);
		servicesUnpublished.add(service);
		service.addListener(createServiceListener(service), sameThreadExecutor());
		return service;
	}

	protected abstract ServicePublisherService createJaxWsServiceInternal(final String contextPath, final Object endpointImpl);

	@Override
	public ServicePublisherService createWebSocketService(final String contextPath, final WebSocketServlet webSocketServlet) {
		final ServicePublisherService service = createWebSocketServiceInternal(contextPath, webSocketServlet);
		servicesUnpublished.add(service);
		service.addListener(createServiceListener(service), sameThreadExecutor());
		return service;
	}

	protected abstract ServicePublisherService createWebSocketServiceInternal(final String contextPath,
															  final WebSocketServlet webSocketServlet);

	protected Service.Listener createServiceListener(final Service service) {
		return new Service.Listener() {
			@Override
			public void starting() {
				// nothing to do
			}

			@Override
			public void running() {
				synchronized (servicesPublished) {
					synchronized (servicesUnpublished) {
						servicesUnpublished.remove(service);
						servicesPublished.add(service);
					}
				}
			}

			@Override
			public void stopping(final Service.State from) {
				// nothing to do
			}

			@Override
			public void terminated(final Service.State from) {
				synchronized (servicesPublished) {
					servicesPublished.remove(service);
				}
			}

			@Override
			public void failed(final Service.State from, final Throwable failure) {
				// nothing to do
			}
		};
	}

}

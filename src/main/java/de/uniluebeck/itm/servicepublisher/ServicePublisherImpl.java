package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.http.spi.JettyHttpServerProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import java.net.InetSocketAddress;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;

@Singleton
class ServicePublisherImpl extends AbstractService implements ServicePublisher {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherImpl.class);

	private static final String HTTP_SERVER_PROVIDER = "com.sun.net.httpserver.HttpServerProvider";

	private final ServicePublisherConfig config;

	private final List<Service> servicesPublished = newArrayList();

	private Server server;

	private ServletContextHandler rootContext;

	@Inject
	public ServicePublisherImpl(final ServicePublisherConfig config) {

		this.config = config;

		server = new Server(InetSocketAddress.createUnresolved(config.hostname, config.port));

		// set up JAX-WS support for Jetty
		System.setProperty(HTTP_SERVER_PROVIDER, JettyHttpServerProvider.class.getCanonicalName());
		JettyHttpServerProvider.setServer(server);

		rootContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		rootContext.setSessionHandler(new SessionHandler());
		rootContext.setContextPath("/");
		rootContext.setResourceBase(config.resourceBase);
		rootContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		rootContext.addServlet(DefaultServlet.class, "/");
		rootContext.addServlet(JspServlet.class, "*.jsp").setInitParameter("classpath", rootContext.getClassPath());

		final ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
		contextHandlerCollection.addHandler(rootContext);

		server.setHandler(contextHandlerCollection);
	}

	@Override
	protected void doStart() {

		try {

			server.addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
				@Override
				public void lifeCycleStarted(final LifeCycle event) {
					log.info("Started server on port {}", config.port);
					notifyStarted();
				}

				@Override
				public void lifeCycleFailure(final LifeCycle event, final Throwable cause) {
					log.error("Failed to start server on port {} due to the following error: " + cause, cause);
					notifyFailed(cause);
				}
			}
			);
			server.start();

		} catch (Exception e) {
			log.error("Failed to start server on port {} due to the following error: " + e, e);
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {
			for (Service service : newArrayList(servicesPublished)) {
				try {
					service.stopAndWait();
				} catch (Exception e) {
					log.warn("Exception while shutting down service {}", service, e.getMessage());
				}
			}
			server.stop();
			notifyStopped();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	String getHostname() {
		return config.hostname;
	}

	int getPort() {
		return config.port;
	}

	@Override
	public ServicePublisherJaxWsService createJaxWsService(final String contextPath, final Object endpointImpl) {
		final ServicePublisherJaxWsService service = new ServicePublisherJaxWsService(
				this,
				rootContext,
				contextPath,
				endpointImpl
		);
		service.addListener(createServiceListener(service), sameThreadExecutor());
		return service;
	}

	@Override
	public ServicePublisherJaxRsService createJaxRsService(final String contextPath,
														   final Class<? extends Application> applicationClass) {
		final ServicePublisherJaxRsService service = new ServicePublisherJaxRsService(
				rootContext,
				contextPath,
				applicationClass
		);
		service.addListener(createServiceListener(service), sameThreadExecutor());
		return service;
	}

	@Override
	public ServicePublisherWebSocketService createWebSocketService(final String contextPath,
																   final WebSocketServlet webSocketServlet) {
		final ServicePublisherWebSocketService service = new ServicePublisherWebSocketService(
				rootContext,
				contextPath,
				webSocketServlet
		);
		service.addListener(createServiceListener(service), sameThreadExecutor());
		return service;
	}

	private Listener createServiceListener(final Service service) {
		return new Listener() {
			@Override
			public void starting() {
				// nothing to do
			}

			@Override
			public void running() {
				synchronized (servicesPublished) {
					servicesPublished.add(service);
				}
			}

			@Override
			public void stopping(final State from) {
				// nothing to do
			}

			@Override
			public void terminated(final State from) {
				synchronized (servicesPublished) {
					servicesPublished.remove(service);
				}
			}

			@Override
			public void failed(final State from, final Throwable failure) {
				// nothing to do
			}
		};
	}
}

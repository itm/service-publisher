package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.xml.ws.Endpoint;

public class ServicePublisherJaxWsService extends AbstractService {

	private final ServicePublisherImpl servicePublisher;

	private final ServletContextHandler rootContext;

	private final String contextPath;

	private final Object endpointImpl;

	private Endpoint endpoint;

	public ServicePublisherJaxWsService(final ServicePublisherImpl servicePublisher,
										final ServletContextHandler rootContext,
										final String contextPath,
										final Object endpointImpl) {
		this.servicePublisher = servicePublisher;
		this.rootContext = rootContext;
		this.contextPath = contextPath;
		this.endpointImpl = endpointImpl;
	}

	@Override
	protected void doStart() {
		try {
			Endpoint.publish(getAddress(contextPath), endpointImpl);
			notifyStarted();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {
			endpoint.stop();
			notifyStopped();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	private String getAddress(final String contextPath) {
		return "http://"
				+ servicePublisher.getHostname() + ":" + servicePublisher.getPort()
				+ (contextPath.startsWith("/") ? contextPath : "/" + contextPath);
	}
}

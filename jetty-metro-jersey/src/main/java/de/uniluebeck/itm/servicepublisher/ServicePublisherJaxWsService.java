package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;

import javax.xml.ws.Endpoint;
import java.net.URI;

public class ServicePublisherJaxWsService extends AbstractService implements ServicePublisherService {

	private final ServicePublisherImpl servicePublisher;

	private final String contextPath;

	private final Object endpointImpl;

	private Endpoint endpoint;

	ServicePublisherJaxWsService(final ServicePublisherImpl servicePublisher,
								 final String contextPath,
								 final Object endpointImpl) {
		this.servicePublisher = servicePublisher;
		this.contextPath = contextPath;
		this.endpointImpl = endpointImpl;
	}

	@Override
	protected void doStart() {
		try {
			endpoint = Endpoint.publish(getAddress(contextPath), endpointImpl);
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
		return "http://localhost:" + servicePublisher.getPort() + (contextPath.startsWith("/") ? contextPath :
				"/" + contextPath);
	}

	@Override
	public URI getURI() {
		return URI.create(getAddress(contextPath));
	}
}

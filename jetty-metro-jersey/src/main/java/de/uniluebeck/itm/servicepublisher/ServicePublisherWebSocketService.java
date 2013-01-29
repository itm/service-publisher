package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.WebSocketServlet;

import java.net.URI;

public class ServicePublisherWebSocketService extends AbstractService implements ServicePublisherService {

	private final ServicePublisherImpl servicePublisher;

	private final ServletContextHandler rootContext;

	private final String contextPath;

	private final WebSocketServlet webSocketServlet;

	private ServletHolder servletHolder;

	ServicePublisherWebSocketService(final ServicePublisherImpl servicePublisher,
									 final ServletContextHandler rootContext,
									 final String contextPath,
									 final WebSocketServlet webSocketServlet) {
		this.servicePublisher = servicePublisher;
		this.rootContext = rootContext;
		this.contextPath = contextPath;
		this.webSocketServlet = webSocketServlet;
	}

	@Override
	protected void doStart() {
		try {
			servletHolder = new ServletHolder(webSocketServlet);
			rootContext.addServlet(servletHolder, contextPath);
			notifyStarted();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {
			servletHolder.stop();
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

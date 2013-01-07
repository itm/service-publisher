package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class ServicePublisherWebSocketService extends AbstractService {

	private final ServicePublisher servicePublisher;

	private final ServletContextHandler rootContext;

	private final String contextPath;

	private final WebSocketServlet webSocketServlet;

	private ServletHolder servletHolder;

	ServicePublisherWebSocketService(final ServicePublisher servicePublisher,
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
}

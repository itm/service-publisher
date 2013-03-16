package de.uniluebeck.itm.servicepublisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import org.eclipse.jetty.http.spi.JettyHttpServerProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.ws.rs.core.Application;

@Singleton
class ServicePublisherImpl extends ServicePublisherBase {

	private static final String HTTP_SERVER_PROVIDER = "com.sun.net.httpserver.HttpServerProvider";

	private Server server;

	private final ContextHandlerCollection contextHandlerCollection;

	@Inject
	public ServicePublisherImpl(@Assisted final ServicePublisherConfig config) {

		super(config);

		server = new Server(config.getPort());

		// set up JAX-WS support for Jetty
		System.setProperty(HTTP_SERVER_PROVIDER, JettyHttpServerProvider.class.getCanonicalName());
		JettyHttpServerProvider.setServer(server);

		contextHandlerCollection = new ContextHandlerCollection();

		server.setHandler(contextHandlerCollection);
	}

	protected void doStartInternal() throws Exception {
		server.start();
	}

	protected void doStopInternal() throws Exception {
		/*
		for (Service service : newArrayList(servicesPublished)) {
			try {
				service.stopAndWait();
			} catch (Exception e) {
				log.warn("Exception while shutting down service {}", service, e.getMessage());
			}
		}
		*/
		server.stop();
	}

	int getPort() {
		return config.getPort();
	}

	protected ServicePublisherService createJaxWsServiceInternal(final String contextPath, final Object endpointImpl) {
		return new ServicePublisherJaxWsService(this, contextPath, endpointImpl);
	}

	protected ServicePublisherService createJaxRsServiceInternal(final String contextPath,
																 final Application application) {
		return new ServicePublisherJaxRsService(this, contextPath, application);
	}

	protected ServicePublisherService createWebSocketServiceInternal(final String contextPath,
																	 final WebSocketServlet webSocketServlet) {
		return new ServicePublisherWebSocketService(this, contextPath, webSocketServlet);
	}

	public ServicePublisherService createServletServiceInternal(final String contextPath, final String resourceBase) {
		return new ServicePublisherServletService(this, contextPath, resourceBase);
	}

	void addHandler(final ServletContextHandler context) {
		contextHandlerCollection.addHandler(context);
	}

	void removeHandler(final ServletContextHandler context) {
		contextHandlerCollection.removeHandler(context);
	}
}

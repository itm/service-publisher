package de.uniluebeck.itm.jettyservicesrunner;

import com.google.common.util.concurrent.AbstractService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.http.spi.JettyHttpServerProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@Singleton
public class JettyServicesRunnerImpl extends AbstractService implements JettyServicesRunner {

	private static final Logger log = LoggerFactory.getLogger(JettyServicesRunnerImpl.class);

	private final JettyServicesRunnerConfig config;

	private Server server;

	private ContextHandler rootContext;

	@Inject
	public JettyServicesRunnerImpl(@Assisted final JettyServicesRunnerConfig config) {
		this.config = config;
	}

	@Override
	public void publishGuiceManaged(final String contextPath, final GuiceFilter guiceFilter) {

		FilterHolder guiceFilterHolder = new FilterHolder(guiceFilter);

		ServletContextHandler guiceContextHandler = new ServletContextHandler(rootContext, contextPath);
		guiceContextHandler.addFilter(guiceFilterHolder, "/*", EnumSet.allOf(DispatcherType.class));
		guiceContextHandler.addServlet(DefaultServlet.class, "/");
	}

	@Override
	public void publishJaxWsEndpoint(final String contextPath, final Object endpointImpl) {
		// TODO implement
	}

	@Override
	public void publishJaxRsResource(final String contextPath, final Object resourceImpl) {
		// TODO implement
	}

	@Override
	public void publishWebSocketServlet(final String contextPath, final WebSocketHandler webSocketHandler) {
		// TODO implement
	}

	@Override
	protected void doStart() {

		try {

			server = new Server(config.port);

			// set up JAX-WS support for Jetty
			System.setProperty(
					"com.sun.net.httpserver.HttpServerProvider",
					JettyHttpServerProvider.class.getCanonicalName()
			);
			JettyHttpServerProvider.setServer(server);

			rootContext = new ContextHandler("/");

			server.setHandler(rootContext);
			server.start();

			log.info("Started server on port {}", config.port);

			notifyStarted();

		} catch (Exception e) {

			log.error("Failed to start server on port {} due to the following error: " + e, e);
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {
			server.stop();
			notifyStopped();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}
}

package de.uniluebeck.itm.jettyservicesrunner;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.AbstractService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.http.spi.JettyHttpServerProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.InetSocketAddress;

@Singleton
public class JettyServicesRunnerImpl extends AbstractService implements JettyServicesRunner {

	private static final Logger log = LoggerFactory.getLogger(JettyServicesRunnerImpl.class);

	private final JettyServicesRunnerConfig config;

	private Server server;

	private ServletContextHandler rootContext;

	private ContextHandlerCollection contextHandlerCollection;

	private final HttpServlet rootServlet = new HttpServlet() {
		@Override
		protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
				throws ServletException, IOException {
			resp.getWriter().write(Resources.toString(Resources.getResource("index.html"), Charsets.UTF_8));
		}
	};

	private final HttpServlet helloServlet = new HttpServlet() {
		private int invocations = 0;

		@Override
		protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
				throws ServletException, IOException {
			resp.getWriter().write("Hello, World! (for the " + (++invocations) + ". time!)");
		}
	};

	private final HttpServlet byeServlet = new HttpServlet() {
		private int invocations = 0;

		@Override
		protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
				throws ServletException, IOException {
			resp.getWriter().write("Bye, Cruel World! (for the " + (++invocations) + ". time!)");
		}
	};

	@Inject
	public JettyServicesRunnerImpl(@Assisted final JettyServicesRunnerConfig config) {
		this.config = config;
	}

	@Override
	public void publishJaxWsEndpoint(final String contextPath, final Object endpointImpl) {
		Endpoint.publish(getAddress(contextPath), endpointImpl);
	}

	@Override
	public void publishJaxRsApplication(final String contextPath, final Class<? extends Application> applicationClass) {
		final ServletContainer servletContainer = new ServletContainer(DemoRestApplication.class);
		rootContext.addServlet(new ServletHolder(servletContainer), contextPath);
	}

	@Override
	public void publishWebSocketServlet(final String contextPath, final WebSocketServlet webSocketServlet) {
		rootContext.addServlet(new ServletHolder(webSocketServlet), contextPath);
	}

	private String getAddress(final String contextPath) {
		return "http://" + config.hostname + ":" + config.port + contextPath;
	}

	@Override
	protected void doStart() {

		try {

			server = new Server(InetSocketAddress.createUnresolved(config.hostname, config.port));

			// set up JAX-WS support for Jetty
			System.setProperty(
					"com.sun.net.httpserver.HttpServerProvider",
					JettyHttpServerProvider.class.getCanonicalName()
			);
			JettyHttpServerProvider.setServer(server);

			rootContext = new ServletContextHandler();
			rootContext.addServlet(new ServletHolder(rootServlet), "/");
			rootContext.addServlet(new ServletHolder(helloServlet), "/hello");

			contextHandlerCollection = new ContextHandlerCollection();
			contextHandlerCollection.addHandler(rootContext);

			server.setHandler(contextHandlerCollection);
			server.start();

			rootContext.addServlet(new ServletHolder(byeServlet), "/bye");

			/*
			final ServletContainer servletContainer = new ServletContainer(DemoRestApplication.class);
			rootContext.addServlet(new ServletHolder(servletContainer), "/rest/*");
			*/

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

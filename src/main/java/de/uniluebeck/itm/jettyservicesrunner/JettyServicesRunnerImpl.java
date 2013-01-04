package de.uniluebeck.itm.jettyservicesrunner;

import com.google.common.util.concurrent.AbstractService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.http.spi.JettyHttpServerProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import javax.xml.ws.Endpoint;
import java.net.InetSocketAddress;

@Singleton
public class JettyServicesRunnerImpl extends AbstractService implements JettyServicesRunner {

	private static final Logger log = LoggerFactory.getLogger(JettyServicesRunnerImpl.class);

	private static final String HTTP_SERVER_PROVIDER = "com.sun.net.httpserver.HttpServerProvider";

	private final JettyServicesRunnerConfig config;

	private Server server;

	private ServletContextHandler rootContext;

	@Inject
	public JettyServicesRunnerImpl(@Assisted final JettyServicesRunnerConfig config) {

		this.config = config;

		server = new Server(InetSocketAddress.createUnresolved(config.hostname, config.port));

		// set up JAX-WS support for Jetty
		System.setProperty(HTTP_SERVER_PROVIDER, JettyHttpServerProvider.class.getCanonicalName());
		JettyHttpServerProvider.setServer(server);

		rootContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		rootContext.setSessionHandler(new SessionHandler());
		rootContext.setContextPath("/");
		rootContext.setResourceBase("src/main/webapp/");
		rootContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		rootContext.addServlet(DefaultServlet.class, "/");
		rootContext.addServlet(JspServlet.class, "*.jsp").setInitParameter("classpath", rootContext.getClassPath());

		final ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
		contextHandlerCollection.addHandler(rootContext);

		server.setHandler(contextHandlerCollection);
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
			server.stop();
			notifyStopped();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}
}

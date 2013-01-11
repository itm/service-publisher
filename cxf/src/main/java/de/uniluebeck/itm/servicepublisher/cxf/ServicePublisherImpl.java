package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniluebeck.itm.servicepublisher.ServicePublisherBase;
import de.uniluebeck.itm.servicepublisher.ServicePublisherConfig;
import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.ws.rs.core.Application;

@Singleton
class ServicePublisherImpl extends ServicePublisherBase {

	private org.eclipse.jetty.server.Server server;

	private ServletContextHandler rootContext;

	@Inject
	public ServicePublisherImpl(final ServicePublisherConfig config) {

		super(config);

		// Start up the jetty embedded server
		server = new org.eclipse.jetty.server.Server(config.getPort());

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
	protected void doStartInternal() throws Exception {
		server.start();
	}

	@Override
	protected void doStopInternal() throws Exception {
		server.stop();
	}

	@Override
	protected Service createJaxWsServiceInternal(final String contextPath, final Object endpointImpl) {
		return new ServicePublisherJaxWsService(rootContext, getAddress(contextPath), endpointImpl);
	}

	@Override
	protected Service createJaxRsServiceInternal(final String contextPath,
												 final Class<? extends Application> applicationClass) {
		return new ServicePublisherJaxRsService(rootContext, contextPath, applicationClass);
	}

	@Override
	protected Service createWebSocketServiceInternal(final String contextPath,
													 final WebSocketServlet webSocketServlet) {
		return new ServicePublisherWebSocketService(rootContext, contextPath, webSocketServlet);
	}

	private String getAddress(final String contextPath) {
		return "http://localhost:" + config.getPort() + contextPath;
	}
}

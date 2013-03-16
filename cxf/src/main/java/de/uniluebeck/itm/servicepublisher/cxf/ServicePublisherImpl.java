package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniluebeck.itm.servicepublisher.ServicePublisherBase;
import de.uniluebeck.itm.servicepublisher.ServicePublisherConfig;
import de.uniluebeck.itm.servicepublisher.ServicePublisherService;
import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;

@Singleton
class ServicePublisherImpl extends ServicePublisherBase {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherImpl.class);

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
		final String resourceBase = this.getClass().getResource("src/main/webapp/").toString();
		log.info("Setting resource base: {}", resourceBase);
		rootContext.setResourceBase(resourceBase);
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
	protected ServicePublisherService createJaxWsServiceInternal(final String contextPath, final Object endpointImpl) {
		return new ServicePublisherJaxWsService(this, rootContext, getAddress(contextPath), endpointImpl);
	}

	@Override
	protected ServicePublisherService createJaxRsServiceInternal(final String contextPath, final Application application) {
		return new ServicePublisherJaxRsService(this, rootContext, contextPath, application);
	}

	@Override
	protected ServicePublisherService createWebSocketServiceInternal(final String contextPath,
													 final WebSocketServlet webSocketServlet) {
		return new ServicePublisherWebSocketService(this, rootContext, contextPath, webSocketServlet);
	}

	String getAddress(final String contextPath) {
		return "http://localhost:" + config.getPort() + contextPath;
	}

	@Override
	public ServicePublisherService createServletService(final String contextPath, final String resourceBase) {
		throw new RuntimeException("TODO implement");
	}
}

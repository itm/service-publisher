package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.servicepublisher.ServicePublisherBase;
import de.uniluebeck.itm.servicepublisher.ServicePublisherConfig;
import de.uniluebeck.itm.servicepublisher.ServicePublisherService;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;

@Singleton
class ServicePublisherImpl extends ServicePublisherBase {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherImpl.class);

	private final ContextHandlerCollection contextHandlerCollection;

	private final org.eclipse.jetty.server.Server server;

	private final CXFNonSpringServlet cxfServlet;

	@Inject
	public ServicePublisherImpl(@Assisted final ServicePublisherConfig config) {

		super(config);

		System.setProperty(BusFactory.BUS_FACTORY_PROPERTY_NAME, "org.apache.cxf.bus.CXFBusFactory");

		// Start up the jetty embedded server
		server = new org.eclipse.jetty.server.Server(config.getPort());
		contextHandlerCollection = new ContextHandlerCollection();
		server.setHandler(contextHandlerCollection);

		ServletContextHandler soapContext = new ServletContextHandler(
				contextHandlerCollection,
				"/",
				ServletContextHandler.SESSIONS
		);

		cxfServlet = new CXFNonSpringServlet();
		ServletHolder servlet = new ServletHolder(cxfServlet);
		servlet.setForcedPath("soap");
		servlet.setInitParameter("hide-service-list-page", "true");
		soapContext.addServlet(servlet, "/soap/*");
	}

	@Override
	protected void doStartInternal() throws Exception {

		server.start();

		Bus bus = cxfServlet.getBus();
		BusFactory.setDefaultBus(bus);
	}

	@Override
	protected void doStopInternal() throws Exception {
		server.stop();
	}

	@Override
	protected ServicePublisherService createJaxWsServiceInternal(final String contextPath, final Object endpointImpl) {
		final String contextPath1 = contextPath.startsWith("/soap") ? contextPath : "/soap" + contextPath;
		return new ServicePublisherJaxWsService(this, contextPath1, endpointImpl);
	}

	@Override
	protected ServicePublisherService createJaxRsServiceInternal(final String contextPath, final Application application) {
		final String contextPath1 = contextPath.startsWith("/rest") ? contextPath : "/rest" + contextPath;
		return new ServicePublisherJaxRsService(this, contextPath1, application);
	}

	@Override
	protected ServicePublisherService createWebSocketServiceInternal(final String contextPath,
													 final WebSocketServlet webSocketServlet) {
		final String contextPath1 = contextPath.startsWith("/ws") ? contextPath : "/ws" + contextPath;
		return new ServicePublisherWebSocketService(this, contextPath1, webSocketServlet);
	}

	@Override
	public ServicePublisherService createServletServiceInternal(final String contextPath, final String resourceBase) {
		return new ServicePublisherServletService(this, contextPath, resourceBase);
	}

	String getAddress(final String contextPath) {
		return "http://localhost:" + config.getPort() + contextPath;
	}

	public void addHandler(final ServletContextHandler contextHandler) {
		contextHandlerCollection.addHandler(contextHandler);
	}

	public void removeHandler(final ServletContextHandler contextHandler) {
		contextHandlerCollection.removeHandler(contextHandler);
	}
}

package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.servicepublisher.ServicePublisherBase;
import de.uniluebeck.itm.servicepublisher.ServicePublisherConfig;
import de.uniluebeck.itm.servicepublisher.ServicePublisherService;
import org.apache.cxf.BusFactory;
import org.apache.shiro.config.Ini;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.annotation.Nullable;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.ws.rs.core.Application;
import java.util.EnumSet;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ServicePublisherImpl extends ServicePublisherBase {

	private final ContextHandlerCollection contextHandlerCollection;

	private final org.eclipse.jetty.server.Server server;

	private final ServletContextHandler soapContext;

	@Inject
	public ServicePublisherImpl(@Assisted final ServicePublisherConfig config) {

		super(config);

		System.setProperty(BusFactory.BUS_FACTORY_PROPERTY_NAME, "org.apache.cxf.bus.CXFBusFactory");

		// Start up the jetty embedded server
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setThreadPool(new QueuedThreadPool());
		connector.setPort(config.getPort());

		server = new org.eclipse.jetty.server.Server();
		server.addConnector(connector);

		contextHandlerCollection = new ContextHandlerCollection();
		server.setHandler(contextHandlerCollection);

		soapContext = new ServletContextHandler();
		soapContext.setContextPath("/soap");
		soapContext.setSessionHandler(new SessionHandler());

		addHandler(soapContext);
	}

	@Override
	protected void doStartInternal() throws Exception {
		server.start();
		soapContext.start();
	}

	@Override
	protected void doStopInternal() throws Exception {
		server.stop();
	}

	@Override
	protected ServicePublisherService createJaxWsServiceInternal(String contextPath, final Object endpointImpl,
																 @Nullable final Ini shiroIni) {
		contextPath = contextPath.startsWith("/soap") ? contextPath.substring("/soap".length()) : contextPath;
		return new ServicePublisherJaxWsService(this, contextPath, endpointImpl, shiroIni);
	}

	@Override
	protected ServicePublisherService createJaxRsServiceInternal(final String contextPath,
																 final Application application,
																 @Nullable final Ini shiroIni) {
		return new ServicePublisherJaxRsService(this, contextPath, application, shiroIni);
	}

	@Override
	protected ServicePublisherService createWebSocketServiceInternal(final String contextPath,
																	 final WebSocketServlet webSocketServlet) {
		return new ServicePublisherWebSocketService(this, contextPath, webSocketServlet);
	}

	@Override
	public ServicePublisherService createServletServiceInternal(final String contextPath, final String resourceBase,
																final Map<String, String> initParams,
																@Nullable final Ini shiroIni,
																final Filter... filters) {
		return new ServicePublisherServletService(this, contextPath, resourceBase, initParams, shiroIni, filters);
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

	protected void addShiroFilter(final ServletContextHandler servletContextHandler, final Ini ini) {

		checkNotNull(servletContextHandler);
		checkNotNull(ini);

		final ServicePublisherEnvironment environment = new ServicePublisherEnvironment(ini);
		final ServicePublisherEnvironmentLoaderListener loaderListener =
				new ServicePublisherEnvironmentLoaderListener(environment);

		final FilterHolder filterHolder = new FilterHolder();
		filterHolder.setDisplayName("ShiroFilter");
		filterHolder.setHeldClass(ShiroFilter.class);

		servletContextHandler.addFilter(filterHolder, "/*", EnumSet.of(
				DispatcherType.ASYNC,
				DispatcherType.ERROR,
				DispatcherType.FORWARD,
				DispatcherType.INCLUDE,
				DispatcherType.REQUEST
		)
		);

		servletContextHandler.addEventListener(loaderListener);
	}

	public ServletContextHandler getSoapContext() {
		return soapContext;
	}
}

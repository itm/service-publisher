package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.servicepublisher.ServicePublisherBase;
import de.uniluebeck.itm.servicepublisher.ServicePublisherConfig;
import de.uniluebeck.itm.servicepublisher.ServicePublisherService;
import de.uniluebeck.itm.util.files.FileUtils;
import org.apache.cxf.BusFactory;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.annotation.Nullable;
import javax.servlet.DispatcherType;
import javax.ws.rs.core.Application;
import java.io.File;
import java.util.EnumSet;
import java.util.Map;

@Singleton
class ServicePublisherImpl extends ServicePublisherBase {

	private final ContextHandlerCollection contextHandlerCollection;

	private final org.eclipse.jetty.server.Server server;

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
	protected ServicePublisherService createServletServiceInternal(final String contextPath, final String resourceBase,
																   @Nullable final File shiroIni,
																   final Map<String, String> initParams) {
		return new ServicePublisherServletService(this, contextPath, resourceBase, shiroIni, initParams);
	}

	@Override
	protected ServicePublisherService createJaxRsServiceInternal(final String contextPath,
																 final Application application,
																 @Nullable final File shiroIni) {
		return new ServicePublisherJaxRsService(this, contextPath, application, shiroIni);
	}

	@Override
	protected ServicePublisherService createJaxWsServiceInternal(final String contextPath, final Object endpointImpl,
																 @Nullable final File shiroIni) {
		return new ServicePublisherJaxWsService(this, contextPath, endpointImpl, shiroIni);
	}

	@Override
	protected ServicePublisherService createWebSocketServiceInternal(final String contextPath,
																	 final WebSocketServlet webSocketServlet,
																	 @Nullable final File shiroIni) {
		return new ServicePublisherWebSocketService(this, contextPath, webSocketServlet, shiroIni);
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

	protected void addShiroFiltersIfNotNull(final ServletContextHandler servletContextHandler,
											@Nullable final File shiroIni) {

		if (shiroIni != null) {

			FileUtils.assertFileExistsAndIsReadable(shiroIni);

			servletContextHandler.setInitParameter("shiroConfigLocations", "file:" + shiroIni.getAbsolutePath());

			final FilterHolder filterHolder = new FilterHolder();
			filterHolder.setDisplayName("ShiroFilter");
			filterHolder.setHeldClass(ShiroFilter.class);
			filterHolder.setInitParameter("shiroConfigLocations", "file:" + shiroIni.getAbsolutePath());

			servletContextHandler.addFilter(filterHolder, "/*", EnumSet.of(
					DispatcherType.ASYNC,
					DispatcherType.ERROR,
					DispatcherType.FORWARD,
					DispatcherType.INCLUDE,
					DispatcherType.REQUEST
			)
			);
			servletContextHandler.addEventListener(new EnvironmentLoaderListener());
		}
	}
}

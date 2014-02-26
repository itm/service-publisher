package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.common.util.concurrent.AbstractService;
import de.uniluebeck.itm.servicepublisher.ServicePublisherService;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

class ServicePublisherJaxRsService extends AbstractService implements ServicePublisherService {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherJaxRsService.class);

	private final ServicePublisherImpl servicePublisher;

	private final String contextPath;

	private final Application application;

	@Nullable
	private final Ini shiroIni;

	private ServletContextHandler contextHandler;

	public ServicePublisherJaxRsService(final ServicePublisherImpl servicePublisher,
										final String contextPath,
										final Application application,
										@Nullable final Ini shiroIni) {
		this.servicePublisher = servicePublisher;
		this.contextPath = contextPath;
		this.application = application;
		this.shiroIni = shiroIni;
	}

	@Override
	protected void doStart() {

		log.trace("ServicePublisherImpl$ServicePublisherJaxRsService.doStart()");
		log.info(
				"Publishing REST service {} with context path {}", application.getClass().getSimpleName(), contextPath
		);

		try {

			final CXFNonSpringJaxrsServlet jaxrsServlet = new CXFNonSpringJaxrsServlet() {
				@Override
				protected Object createSingletonInstance(final Class<?> cls, final Map<String, List<String>> props,
														 final ServletConfig sc) throws ServletException {
					// workaround to not have CXF try to create the application instance but pass in one
					return application;
				}
			};

			final ServletHolder servletHolder = new ServletHolder(jaxrsServlet);

			final Map<String, String> params = newHashMap();
			params.put("javax.ws.rs.Application", application.getClass().getCanonicalName());
			params.put("jaxrs.extensions", "xml=application/xml\njson=application/json");
			params.put("jaxrs.providers", JSONProvider.class.getCanonicalName());
			servletHolder.setInitParameters(params);

			contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
			contextHandler.setSessionHandler(new SessionHandler());
			contextHandler.setContextPath(contextPath);
			contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
			contextHandler.addServlet(servletHolder, "/*");

			if (shiroIni != null && !CollectionUtils.isEmpty(shiroIni)) {
				servicePublisher.addShiroFilter(contextHandler, shiroIni);
			}

			servicePublisher.addHandler(contextHandler);
			contextHandler.start();

			servicePublisher.addHandler(contextHandler);

			notifyStarted();

		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {

		log.trace("ServicePublisherImpl$ServicePublisherJaxRsService.doStop()");

		try {

			servicePublisher.removeHandler(contextHandler);

			notifyStopped();

		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	public URI getURI() {
		return URI.create(servicePublisher.getAddress(contextPath));
	}

	@Override
	@Nullable
	public ServletContextHandler getServletContextHandler() {
		return contextHandler;
	}
}
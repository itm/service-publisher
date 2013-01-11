package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.common.util.concurrent.AbstractService;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

class ServicePublisherJaxRsService extends AbstractService {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherJaxRsService.class);

	private final ServletContextHandler rootContext;

	private final String contextPath;

	private final Application application;

	public ServicePublisherJaxRsService(final ServletContextHandler rootContext,
										final String contextPath,
										final Application application) {
		this.rootContext = rootContext;
		this.contextPath = contextPath;
		this.application = application;
	}

	@Override
	protected void doStart() {

		log.trace("ServicePublisherImpl$ServicePublisherJaxRsService.doStart()");

		try {

			final ServletHolder servletHolder = new ServletHolder(new CXFNonSpringJaxrsServlet());

			final Map<String, String> params = newHashMap();
			params.put("javax.ws.rs.Application", application.getClass().getCanonicalName());
			params.put("jaxrs.extensions", "xml=application/xml\njson=application/json");
			servletHolder.setInitParameters(params);

			rootContext.addServlet(servletHolder, contextPath);

			/*
			JAXRSServerFactoryBean serverFactoryBean = new JAXRSServerFactoryBean();
			serverFactoryBean.setResourceClasses(applicationClazz);
			serverFactoryBean.setResourceProvider(applicationClazz, new SingletonResourceProvider(instance));
			serverFactoryBean.setAddress(getAddress(contextPath));
			serverFactoryBean.create();
			*/

			notifyStarted();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {

		log.trace("ServicePublisherImpl$ServicePublisherJaxRsService.doStop()");

		try {
			notifyStopped();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

}
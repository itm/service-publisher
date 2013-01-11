package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.ws.rs.core.Application;

public class ServicePublisherJaxRsService extends AbstractService {

	private final ServletContextHandler rootContext;

	private final String contextPath;

	private final Class<? extends Application> applicationClass;

	private ServletHolder servletHolder;

	ServicePublisherJaxRsService(final ServletContextHandler rootContext,
								 final String contextPath,
								 final Class<? extends Application> applicationClass) {
		this.rootContext = rootContext;
		this.contextPath = contextPath;
		this.applicationClass = applicationClass;
	}

	@Override
	protected void doStart() {
		try {
			servletHolder = new ServletHolder(new ServletContainer(applicationClass));
			rootContext.addServlet(servletHolder, contextPath);
			notifyStarted();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {
			servletHolder.stop();
			notifyStopped();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}
}

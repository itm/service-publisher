package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class ServicePublisherJaxRsService extends AbstractService {

	private final ServletContextHandler rootContext;

	private final String contextPath;

	private final Application application;

	private ServletHolder servletHolder;

	ServicePublisherJaxRsService(final ServletContextHandler rootContext,
								 final String contextPath,
								 final Application application) {
		this.rootContext = rootContext;
		this.contextPath = contextPath;
		this.application = application;
	}

	@Override
	protected void doStart() {
		try {
			servletHolder = new ServletHolder(new ServletContainer(new ApplicationWrapper(application)));
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

	private static class ApplicationWrapper extends Application {

		private final Application application;

		private ApplicationWrapper(final Application application) {
			this.application = application;
		}

		@Override
		public Set<Class<?>> getClasses() {
			final HashSet<Class<?>> applicationClasses = newHashSet(application.getClasses());
			applicationClasses.add(ServicePublisherJsonMessageBodyWriter.class);
			return applicationClasses;
		}

		@Override
		public Set<Object> getSingletons() {
			return application.getSingletons();
		}
	}

}

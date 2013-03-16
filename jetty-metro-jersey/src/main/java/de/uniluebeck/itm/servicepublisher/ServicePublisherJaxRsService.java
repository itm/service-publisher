package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class ServicePublisherJaxRsService extends AbstractService implements ServicePublisherService {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherJaxRsService.class);

	private final ServicePublisherImpl servicePublisher;

	private final String contextPath;

	private final Application application;

	private ServletContextHandler contextHandler;

	ServicePublisherJaxRsService(final ServicePublisherImpl servicePublisher,
								 final String contextPath,
								 final Application application) {
		this.servicePublisher = servicePublisher;
		this.contextPath = contextPath;
		this.application = application;
	}

	@Override
	protected void doStart() {
		try {

			final ServletHolder servletHolder =
					new ServletHolder(new ServletContainer(new ApplicationWrapper(application)));

			contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
			contextHandler.setSessionHandler(new SessionHandler());
			contextHandler.setContextPath(contextPath);
			contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
			contextHandler.addServlet(servletHolder, "/*");

			servicePublisher.addHandler(contextHandler);
			contextHandler.start();

			log.info("Published JAX-RS service under: {}", contextPath);

			notifyStarted();

		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {

			servicePublisher.removeHandler(contextHandler);

			notifyStopped();

		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	private String getAddress(final String contextPath) {
		return "http://localhost:" + servicePublisher.getPort() + (contextPath.startsWith("/") ? contextPath :
				"/" + contextPath);
	}

	@Override
	public URI getURI() {
		return URI.create(getAddress(contextPath));
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

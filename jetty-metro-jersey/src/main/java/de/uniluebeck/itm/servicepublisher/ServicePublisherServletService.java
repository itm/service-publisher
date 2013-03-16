package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.AbstractService;
import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class ServicePublisherServletService extends AbstractService implements ServicePublisherService {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherServletService.class);

	private final ServicePublisherImpl servicePublisher;

	private final ServletContextHandler rootContext;

	private final String contextPath;

	private final String resourceBase;

	private ServletContextHandler context;

	public ServicePublisherServletService(final ServicePublisherImpl servicePublisher,
										  final ServletContextHandler rootContext,
										  final String contextPath,
										  final String resourceBase) {
		this.servicePublisher = servicePublisher;
		this.rootContext = rootContext;
		this.contextPath = contextPath;
		this.resourceBase = resourceBase;
	}

	@Override
	protected void doStart() {
		try {

			context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setSessionHandler(new SessionHandler());
			context.setContextPath(contextPath);
			context.setResourceBase(resourceBase);
			context.setClassLoader(Thread.currentThread().getContextClassLoader());
			context.addServlet(DefaultServlet.class, "/");
			context.addServlet(JspServlet.class, "*.jsp").setInitParameter("classpath", rootContext.getClassPath());

			servicePublisher.getContextHandlerCollection().addHandler(context);
			context.start();

			log.info("Published servlet service under {} with resource base: {}", contextPath, resourceBase);

			notifyStarted();

		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {

			servicePublisher.getContextHandlerCollection().removeHandler(context);

			notifyStopped();

		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	public URI getURI() {
		return URI.create("http://localhost:" + servicePublisher.getPort() +
				(contextPath.startsWith("/") ? contextPath : "/" + contextPath)
		);
	}
}

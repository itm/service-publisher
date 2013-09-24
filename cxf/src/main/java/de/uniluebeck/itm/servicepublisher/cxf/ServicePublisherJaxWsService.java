package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.common.util.concurrent.AbstractService;
import de.uniluebeck.itm.servicepublisher.ServicePublisherService;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.ws.Endpoint;
import java.io.File;
import java.net.URI;

class ServicePublisherJaxWsService extends AbstractService implements ServicePublisherService {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherJaxWsService.class);

	private final ServicePublisherImpl servicePublisher;

	private final String contextPath;

	private final Object endpointImpl;

	@Nullable
	private final File shiroIni;

	private Endpoint endpoint;

	private ServletContextHandler contextHandler;

	public ServicePublisherJaxWsService(final ServicePublisherImpl servicePublisher,
										final String contextPath,
										final Object endpointImpl, @Nullable final File shiroIni) {
		this.servicePublisher = servicePublisher;
		this.contextPath = contextPath;
		this.endpointImpl = endpointImpl;
		this.shiroIni = shiroIni;
	}

	@Override
	protected void doStart() {
		try {

			log.info("Publishing SOAP web service {} under context path /soap{}", endpointImpl.getClass().getSimpleName(),
					contextPath
			);

			contextHandler = new ServletContextHandler();
			contextHandler.setContextPath(contextPath);
			contextHandler.setSessionHandler(new SessionHandler());

			servicePublisher.addShiroFiltersIfNotNull(contextHandler, shiroIni);

			final CXFNonSpringServlet cxfServlet = new CXFNonSpringServlet() {
				@Override
				public void init(final ServletConfig sc) throws ServletException {
					super.init(sc);
					BusFactory.setDefaultBus(getBus());
				}
			};
			contextHandler.addServlet(new ServletHolder(cxfServlet), "/*");

			servicePublisher.addHandler(contextHandler);
			endpoint = Endpoint.publish(contextPath, endpointImpl);

			notifyStarted();

		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {
			endpoint.stop();
			servicePublisher.removeHandler(contextHandler);
			notifyStopped();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	public URI getURI() {
		return URI.create(servicePublisher.getAddress("/soap" + contextPath));
	}

	@Nullable
	@Override
	public ServletContextHandler getServletContextHandler() {
		return null;
	}
}
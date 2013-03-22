package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.common.util.concurrent.AbstractService;
import de.uniluebeck.itm.servicepublisher.ServicePublisherService;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.ws.Endpoint;
import java.net.URI;

class ServicePublisherJaxWsService extends AbstractService implements ServicePublisherService {

	private final ServicePublisherImpl servicePublisher;

	private final String contextPath;

	private final Object endpointImpl;

	private Endpoint endpoint;

	public ServicePublisherJaxWsService(final ServicePublisherImpl servicePublisher,
										final String contextPath,
										final Object endpointImpl) {
		this.servicePublisher = servicePublisher;
		this.contextPath = contextPath;
		this.endpointImpl = endpointImpl;
	}

	@Override
	protected void doStart() {
		try {

			ServletContextHandler context = new ServletContextHandler();
			context.setContextPath(contextPath);
			context.setSessionHandler(new SessionHandler());

			CXFNonSpringServlet cxfServlet = new CXFNonSpringServlet() {
				@Override
				public void init(final ServletConfig sc) throws ServletException {
					super.init(sc);
					BusFactory.setDefaultBus(getBus());
				}
			};
			context.addServlet(new ServletHolder(cxfServlet), "/*");

			servicePublisher.addHandler(context);
			context.start();

			endpoint = Endpoint.publish("/", endpointImpl);

			notifyStarted();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {
			endpoint.stop();
			notifyStopped();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	public URI getURI() {
		return URI.create(servicePublisher.getAddress(contextPath));
	}
}
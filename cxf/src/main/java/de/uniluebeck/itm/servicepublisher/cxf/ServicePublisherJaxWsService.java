package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.common.util.concurrent.AbstractService;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

class ServicePublisherJaxWsService extends AbstractService {

	private final ServletContextHandler rootContext;

	private final String address;

	private final Object endpointImpl;

	private ServletHolder cxfServletHolder;

	public ServicePublisherJaxWsService(final ServletContextHandler rootContext, final String address,
										final Object endpointImpl) {
		this.rootContext = rootContext;
		this.address = address;
		this.endpointImpl = endpointImpl;
	}

	@Override
	protected void doStart() {
		try {
			final ServicePublisherCxfServlet cxfServlet = new ServicePublisherCxfServlet(address, endpointImpl);
			cxfServletHolder = new ServletHolder(cxfServlet);
			rootContext.addServlet(cxfServletHolder, "/");
			notifyStarted();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

	@Override
	protected void doStop() {
		try {
			cxfServletHolder.stop();
			notifyStarted();
		} catch (Exception e) {
			notifyFailed(e);
		}
	}

}
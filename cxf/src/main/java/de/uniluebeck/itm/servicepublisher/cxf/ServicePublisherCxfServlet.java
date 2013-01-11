package de.uniluebeck.itm.servicepublisher.cxf;

import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;

public class ServicePublisherCxfServlet extends CXFNonSpringServlet {

	private final String address;

	private final Object endpointImpl;

	private Logger logger = LoggerFactory.getLogger(ServicePublisherCxfServlet.class);

	private Endpoint endpoint;

	public ServicePublisherCxfServlet(final String address, final Object endpointImpl) {
		super();
		this.address = address;
		this.endpointImpl = endpointImpl;
	}

	@Override
	public void loadBus(ServletConfig servletConfig) {
		super.loadBus(servletConfig);
		logger.info("Loading CXF servlet...");
		BusFactory.setDefaultBus(this.getBus());
		endpoint = Endpoint.publish(address, endpointImpl);
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}
}
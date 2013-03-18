package de.uniluebeck.itm.servicepublisher.cxf;

import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;

public class ServicePublisherCxfServlet extends CXFNonSpringServlet {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherCxfServlet.class);

	private final String address;

	private final Object endpointImpl;

	public ServicePublisherCxfServlet(final String address, final Object endpointImpl) {
		this.address = address;
		this.endpointImpl = endpointImpl;
	}

	@Override
	public void loadBus(ServletConfig servletConfig) {
		super.loadBus(servletConfig);
		log.info("Loading CXF servlet...");
		BusFactory.setDefaultBus(this.getBus());
		Endpoint.publish(address, endpointImpl);
	}
}
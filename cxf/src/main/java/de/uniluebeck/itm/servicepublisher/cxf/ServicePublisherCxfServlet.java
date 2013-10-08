package de.uniluebeck.itm.servicepublisher.cxf;

import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;

public class ServicePublisherCxfServlet extends CXFNonSpringServlet {

	private static final Logger log = LoggerFactory.getLogger(ServicePublisherCxfServlet.class);

	private final String contextPath;

	private final Object endpointImpl;

	private Endpoint endpoint;

	public ServicePublisherCxfServlet(final String contextPath, final Object endpointImpl) {
		this.contextPath = contextPath;
		this.endpointImpl = endpointImpl;
	}

	@Override
	public void loadBus(ServletConfig servletConfig) {
		super.loadBus(servletConfig);
		log.info("Loading CXF servlet...");
		BusFactory.setDefaultBus(this.getBus());
		endpoint = Endpoint.publish(contextPath, endpointImpl);
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}
}
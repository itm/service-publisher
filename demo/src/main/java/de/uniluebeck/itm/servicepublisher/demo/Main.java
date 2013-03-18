package de.uniluebeck.itm.servicepublisher.demo;

import de.uniluebeck.itm.tr.util.Logging;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;
import java.net.InetSocketAddress;

public class Main {

	static {
		Logging.setLoggingDefaults();
	}

	public static class ServicePublisherCxfServlet extends CXFNonSpringServlet {

		private final String address;

		private final Object endpointImpl;

		public ServicePublisherCxfServlet(final String address, final Object endpointImpl) {
			this.address = address;
			this.endpointImpl = endpointImpl;
		}

		@Override
		public void loadBus(ServletConfig servletConfig) {
			super.loadBus(servletConfig);
			BusFactory.setDefaultBus(this.getBus());
			Endpoint.publish(address, endpointImpl);
		}
	}

	public static void main(String[] args) throws Exception {

		// Start up the jetty embedded server
		Server server = new Server(InetSocketAddress.createUnresolved("0.0.0.0", 8080));
		ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
		server.setHandler(contextHandlerCollection);

		ServicePublisherCxfServlet servlet = new ServicePublisherCxfServlet(
				"http://localhost:8080/",
				new DemoSoapService()
		);
		ServletHolder servletHolder = new ServletHolder(servlet);

		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setSessionHandler(new SessionHandler());
		contextHandler.setContextPath("/");
		contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
		contextHandler.addServlet(servletHolder, "");

		contextHandlerCollection.addHandler(contextHandler);
		//contextHandler.start();

		server.start();
	}

}

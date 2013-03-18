/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.uniluebeck.itm.servicepublisher.demo;

import de.uniluebeck.itm.tr.util.Logging;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.jasper.servlet.JspServlet;
import org.apache.log4j.Level;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.xml.ws.Endpoint;

public class NoSpringServletServer {

	static {
		Logging.setLoggingDefaults(Level.INFO);
	}

	Server httpServer;

	protected void run() {
		// setup the system properties
		String busFactory = System.getProperty(BusFactory.BUS_FACTORY_PROPERTY_NAME);
		System.setProperty(BusFactory.BUS_FACTORY_PROPERTY_NAME, "org.apache.cxf.bus.CXFBusFactory");
		try {

			httpServer = new Server(8080);
			ContextHandlerCollection contexts = new ContextHandlerCollection();
			httpServer.setHandler(contexts);

			ServletContextHandler root = new ServletContextHandler(contexts, "/", ServletContextHandler.SESSIONS);

			CXFNonSpringServlet cxf = new CXFNonSpringServlet();
			ServletHolder servlet = new ServletHolder(cxf);
			servlet.setName("cxf");
			servlet.setForcedPath("services");
			root.addServlet(servlet, "/services/*");

			httpServer.start();

			Bus bus = cxf.getBus();
			BusFactory.setDefaultBus(bus);

			Endpoint.publish("/soap/v1.0", new DemoSoapService());
			Endpoint.publish("/soap/v2.0", new DemoSoapService2());

			ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
			contextHandler.setSessionHandler(new SessionHandler());
			contextHandler.setContextPath("/");
			contextHandler.setResourceBase(this.getClass().getResource("/rootcontext").toString());
			contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
			contextHandler.addServlet(DefaultServlet.class, "/");
			contextHandler.addServlet(JspServlet.class, "*.jsp").setInitParameter(
					"classpath",
					contextHandler.getClassPath()
			);
			contexts.addHandler(contextHandler);
			contextHandler.start();

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// clean up the system properties
			if (busFactory != null) {
				System.setProperty(BusFactory.BUS_FACTORY_PROPERTY_NAME, busFactory);
			} else {
				System.clearProperty(BusFactory.BUS_FACTORY_PROPERTY_NAME);
			}
		}
	}

	public void tearDown() throws Exception {
		if (httpServer != null) {
			httpServer.stop();
		}
	}

	public static void main(String[] args) {
		try {
			NoSpringServletServer s = new NoSpringServletServer();
			s.run();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		} finally {
			System.out.println("done!");
		}
	}

}
package de.uniluebeck.itm.servicepublisher.demo;

import com.google.inject.Module;
import de.uniluebeck.itm.servicepublisher.ServicePublisher;
import de.uniluebeck.itm.servicepublisher.ServicePublisherConfig;
import de.uniluebeck.itm.servicepublisher.ServicePublisherFactory;
import de.uniluebeck.itm.servicepublisher.ServicePublisherJettyMetroJerseyModule;
import de.uniluebeck.itm.servicepublisher.cxf.ServicePublisherCxfModule;
import de.uniluebeck.itm.tr.util.Logging;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.Handler;
import java.util.logging.LogManager;

public class Demo {

	static {
		Logging.setLoggingDefaults();

		// Jersey uses java.util.logging - bridge to slf4
		java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (final Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}
		SLF4JBridgeHandler.install();
	}

	public static void main(String[] args) throws InterruptedException {

		final boolean useCxf = args.length > 0 && "cxf".equalsIgnoreCase(args[0]);

		final Class<? extends Module> moduleClass = useCxf ?
				ServicePublisherCxfModule.class :
				ServicePublisherJettyMetroJerseyModule.class;

		final int port = 8080;
		final String resourceBase = "demo/src/main/webapp";

		final ServicePublisherConfig config = new ServicePublisherConfig(port, moduleClass, resourceBase);
		final ServicePublisher servicePublisher = ServicePublisherFactory.create(config);

		servicePublisher.createJaxRsService("/rest/v1.0/*", DemoRestApplication.class);
		servicePublisher.createJaxRsService("/rest/v2.0/*", DemoRestApplication2.class);
		servicePublisher.createJaxWsService("/soap/v1.0", new DemoSoapService());
		servicePublisher.createJaxWsService("/soap/v2.0", new DemoSoapService2());
		servicePublisher.createWebSocketService("/ws/v1.0/*", new DemoWebSocketServlet());
		servicePublisher.createWebSocketService("/ws/v2.0/*", new DemoWebSocketServlet());

		servicePublisher.startAndWait();

		Runtime.getRuntime().addShutdownHook(
				new Thread("Demo-ShutdownThread") {
					@Override
					public void run() {
						servicePublisher.stopAndWait();
					}
				}
		);
	}
}

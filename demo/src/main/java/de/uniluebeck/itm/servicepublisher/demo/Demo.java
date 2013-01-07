package de.uniluebeck.itm.servicepublisher.demo;

import com.google.inject.Guice;
import de.uniluebeck.itm.servicepublisher.ServicePublisher;
import de.uniluebeck.itm.servicepublisher.ServicePublisherConfig;
import de.uniluebeck.itm.servicepublisher.ServicePublisherModule;
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
		for (int i = 0; i < handlers.length; i++) {
			rootLogger.removeHandler(handlers[i]);
		}
		SLF4JBridgeHandler.install();
	}

	public static void main(String[] args) throws InterruptedException {

		final ServicePublisherConfig config = new ServicePublisherConfig(8888);
		config.resourceBase = "demo/src/main/webapp";

		final ServicePublisher servicePublisher = Guice
				.createInjector(new ServicePublisherModule(config))
				.getInstance(ServicePublisher.class);

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

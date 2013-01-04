package de.uniluebeck.itm.jettyservicesrunner;

import com.google.inject.Guice;
import de.uniluebeck.itm.tr.util.Logging;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.Handler;
import java.util.logging.LogManager;

public class JettyServicesRunnerDemo {

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

	public static void main(String[] args) {

		JettyServicesRunnerFactory factory = Guice
				.createInjector(new JettyServicesRunnerModule())
				.getInstance(JettyServicesRunnerFactory.class);

		final JettyServicesRunner runner = factory.create(new JettyServicesRunnerConfig(8888));
		runner.startAndWait();

		runner.publishJaxRsApplication("/rest/*", DemoRestApplication.class);
		runner.publishJaxWsEndpoint("/soap/demo", new DemoSoapService());
		runner.publishWebSocketServlet("/ws/*", new DemoWebSocketServlet());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				runner.stopAndWait();
			}
		}
		);
	}
}

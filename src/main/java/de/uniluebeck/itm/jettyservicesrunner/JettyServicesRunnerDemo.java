package de.uniluebeck.itm.jettyservicesrunner;

import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import de.uniluebeck.itm.tr.util.Logging;
import org.eclipse.jetty.servlet.DefaultServlet;

public class JettyServicesRunnerDemo {

	static {
		Logging.setLoggingDefaults();
	}

	public static void main(String[] args) {

		JettyServicesRunnerFactory factory = Guice
				.createInjector(new JettyServicesRunnerModule())
				.getInstance(JettyServicesRunnerFactory.class);

		final JettyServicesRunner runner = factory.create(new JettyServicesRunnerConfig(8888));
		runner.startAndWait();

		runner.publishGuiceManaged("/test", Guice.createInjector(new ServletModule() {
			@Override
			protected void configureServlets() {
				serve("*").with(DefaultServlet.class);
			}
		}
		).getInstance(GuiceFilter.class)
		);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				runner.stopAndWait();
			}
		});
	}
}

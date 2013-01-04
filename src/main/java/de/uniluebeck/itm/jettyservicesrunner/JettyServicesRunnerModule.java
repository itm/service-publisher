package de.uniluebeck.itm.jettyservicesrunner;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class JettyServicesRunnerModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder()
				.implement(JettyServicesRunner.class, JettyServicesRunnerImpl.class)
				.build(JettyServicesRunnerFactory.class)
		);
	}
}

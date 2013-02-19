package de.uniluebeck.itm.servicepublisher;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ServicePublisherJettyMetroJerseyModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder()
				.implement(ServicePublisher.class, ServicePublisherImpl.class)
				.build(ServicePublisherFactory.class)
		);
	}
}

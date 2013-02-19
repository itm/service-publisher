package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import de.uniluebeck.itm.servicepublisher.ServicePublisher;
import de.uniluebeck.itm.servicepublisher.ServicePublisherFactory;

public class ServicePublisherCxfModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder()
				.implement(ServicePublisher.class, ServicePublisherImpl.class)
				.build(ServicePublisherFactory.class)
		);
	}
}

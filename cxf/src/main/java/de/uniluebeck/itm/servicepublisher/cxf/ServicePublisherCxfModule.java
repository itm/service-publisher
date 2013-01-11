package de.uniluebeck.itm.servicepublisher.cxf;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import de.uniluebeck.itm.servicepublisher.ServicePublisher;
import de.uniluebeck.itm.servicepublisher.ServicePublisherConfig;

public class ServicePublisherCxfModule extends AbstractModule {

	private final ServicePublisherConfig config;

	public ServicePublisherCxfModule(final ServicePublisherConfig config) {
		this.config = config;
	}

	@Override
	protected void configure() {
		bind(ServicePublisherConfig.class).toInstance(config);
		bind(ServicePublisher.class).to(ServicePublisherImpl.class).in(Scopes.SINGLETON);
	}
}

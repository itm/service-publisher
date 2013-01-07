package de.uniluebeck.itm.servicepublisher;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ServicePublisherModule extends AbstractModule {

	private final ServicePublisherConfig config;

	public ServicePublisherModule(final ServicePublisherConfig config) {
		this.config = config;
	}

	@Override
	protected void configure() {
		bind(ServicePublisherConfig.class).toInstance(config);
		bind(ServicePublisher.class).to(ServicePublisherImpl.class).in(Scopes.SINGLETON);
	}
}

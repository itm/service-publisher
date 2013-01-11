package de.uniluebeck.itm.servicepublisher;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ServicePublisherJettyMetroJerseyModule extends AbstractModule {

	private final ServicePublisherConfig config;

	public ServicePublisherJettyMetroJerseyModule(final ServicePublisherConfig config) {
		this.config = config;
	}

	@Override
	protected void configure() {
		bind(ServicePublisherConfig.class).toInstance(config);
		bind(ServicePublisher.class).to(ServicePublisherImpl.class).in(Scopes.SINGLETON);
	}
}

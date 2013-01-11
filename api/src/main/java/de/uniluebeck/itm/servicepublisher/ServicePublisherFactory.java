package de.uniluebeck.itm.servicepublisher;

import com.google.inject.Guice;

import static com.google.common.base.Throwables.propagate;

public abstract class ServicePublisherFactory {

	public static ServicePublisher create(final ServicePublisherConfig config) {
		try {

			return Guice
					.createInjector(config.getModule().getConstructor(ServicePublisherConfig.class).newInstance(config))
					.getInstance(ServicePublisher.class);

		} catch (Exception e) {
			throw propagate(e);
		}
	}

}

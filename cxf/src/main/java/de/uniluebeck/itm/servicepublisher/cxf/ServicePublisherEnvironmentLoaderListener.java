package de.uniluebeck.itm.servicepublisher.cxf;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServicePublisherEnvironmentLoaderListener extends ServicePublisherEnvironmentLoader implements ServletContextListener {

	public ServicePublisherEnvironmentLoaderListener(
			final ServicePublisherEnvironment environment) {
		super(environment);
	}

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		initEnvironment(sce.getServletContext());
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		destroyEnvironment(sce.getServletContext());
	}
}

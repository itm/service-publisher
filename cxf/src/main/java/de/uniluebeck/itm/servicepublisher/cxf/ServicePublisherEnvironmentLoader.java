package de.uniluebeck.itm.servicepublisher.cxf;

import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.web.env.EnvironmentLoader;
import org.apache.shiro.web.env.WebEnvironment;

import javax.servlet.ServletContext;

public class ServicePublisherEnvironmentLoader extends EnvironmentLoader {

	private final ServicePublisherEnvironment environment;

	public ServicePublisherEnvironmentLoader(final ServicePublisherEnvironment environment) {
		this.environment = environment;
	}

	@Override
	protected WebEnvironment createEnvironment(final ServletContext sc) {
		environment.setServletContext(sc);
		LifecycleUtils.init(environment);
		return environment;
	}
}

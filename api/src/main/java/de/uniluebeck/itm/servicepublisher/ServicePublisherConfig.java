package de.uniluebeck.itm.servicepublisher;

import com.google.inject.Module;

import javax.annotation.Nullable;

public class ServicePublisherConfig {

	public static final int DEFAULT_PORT = 8080;

	private static final String DEFAULT_RESOURCE_BASE = "src/main/webapp";

	private int port = DEFAULT_PORT;

	@Nullable
	private String resourceBase = DEFAULT_RESOURCE_BASE;

	private Class<? extends Module> module;

	@SuppressWarnings("unused")
	public ServicePublisherConfig(final int port, final Class<? extends Module> module) {
		this(port, module, null);
	}

	@SuppressWarnings("unused")
	public ServicePublisherConfig(final int port, final Class<? extends Module> module,
								  @Nullable final String resourceBase) {
		this.port = port;
		this.resourceBase = resourceBase;
		this.module = module;
	}

	public Class<? extends Module> getModule() {
		return module;
	}

	public int getPort() {
		return port;
	}

	@Nullable
	public String getResourceBase() {
		return resourceBase;
	}
}

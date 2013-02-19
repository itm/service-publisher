package de.uniluebeck.itm.servicepublisher;

import javax.annotation.Nullable;

public class ServicePublisherConfig {

	public static final int DEFAULT_PORT = 8080;

	private static final String DEFAULT_RESOURCE_BASE = "src/main/webapp";

	private int port = DEFAULT_PORT;

	@Nullable
	private String resourceBase = DEFAULT_RESOURCE_BASE;

	@SuppressWarnings("unused")
	public ServicePublisherConfig(final int port) {
		this(port, null);
	}

	@SuppressWarnings("unused")
	public ServicePublisherConfig(final int port, @Nullable final String resourceBase) {
		this.port = port;
		this.resourceBase = resourceBase;
	}

	public int getPort() {
		return port;
	}

	@Nullable
	public String getResourceBase() {
		return resourceBase;
	}
}

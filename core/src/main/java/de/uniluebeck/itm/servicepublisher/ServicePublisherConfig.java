package de.uniluebeck.itm.servicepublisher;

import javax.annotation.Nullable;

public class ServicePublisherConfig {

	public String hostname = "localhost";

	public int port = 8080;

	@Nullable
	public String resourceBase = "src/main/webapp";

	public ServicePublisherConfig() {
	}

	public ServicePublisherConfig(final int port) {
		this.port = port;
	}

	public ServicePublisherConfig(final String hostname, final int port) {
		this.hostname = hostname;
		this.port = port;
	}
}

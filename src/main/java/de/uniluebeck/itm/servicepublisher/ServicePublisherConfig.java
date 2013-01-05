package de.uniluebeck.itm.servicepublisher;

public class ServicePublisherConfig {

	public String hostname = "localhost";

	public int port = 8080;

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

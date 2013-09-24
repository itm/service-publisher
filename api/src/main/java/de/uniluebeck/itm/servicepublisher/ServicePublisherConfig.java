package de.uniluebeck.itm.servicepublisher;

public class ServicePublisherConfig {

	public static final int DEFAULT_PORT = 8080;

	private int port = DEFAULT_PORT;

	@SuppressWarnings("unused")
	public ServicePublisherConfig(final int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}
}

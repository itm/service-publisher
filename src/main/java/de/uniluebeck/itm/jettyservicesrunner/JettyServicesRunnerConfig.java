package de.uniluebeck.itm.jettyservicesrunner;

public class JettyServicesRunnerConfig {

	public String hostname = null;

	public int port = 8080;

	public JettyServicesRunnerConfig() {
	}

	public JettyServicesRunnerConfig(final int port) {
		this.port = port;
	}

	public JettyServicesRunnerConfig(final String hostname, final int port) {
		this.hostname = hostname;
		this.port = port;
	}
}

package de.uniluebeck.itm.servicepublisher.demo;

import org.eclipse.jetty.websocket.WebSocket;

import java.io.IOException;

import static com.google.common.base.Throwables.propagate;

public class DemoWebSocket implements WebSocket, WebSocket.OnTextMessage {

	private Connection connection;

	@Override
	public void onMessage(final String data) {
		try {
			connection.sendMessage("hello world " + data);
		} catch (IOException e) {
			throw propagate(e);
		}
	}

	@Override
	public void onOpen(final Connection connection) {
		this.connection = connection;
	}

	@Override
	public void onClose(final int closeCode, final String message) {
		this.connection = null;
	}
}

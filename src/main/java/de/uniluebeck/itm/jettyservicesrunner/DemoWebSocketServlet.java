package de.uniluebeck.itm.jettyservicesrunner;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.servlet.http.HttpServletRequest;

public class DemoWebSocketServlet extends WebSocketServlet {

	@Override
	public WebSocket doWebSocketConnect(final HttpServletRequest request, final String protocol) {
		return new DemoWebSocket();
	}
}

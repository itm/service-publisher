package de.uniluebeck.itm.jettyservicesrunner;

import com.google.common.util.concurrent.Service;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.websocket.WebSocketHandler;

public interface JettyServicesRunner extends Service {

	void publishGuiceManaged(String contextPath, GuiceFilter guiceFilter);

	void publishJaxWsEndpoint(String contextPath, Object endpointImpl);

	void publishJaxRsResource(String contextPath, Object resourceImpl);

	void publishWebSocketServlet(String contextPath, WebSocketHandler webSocketHandler);

}

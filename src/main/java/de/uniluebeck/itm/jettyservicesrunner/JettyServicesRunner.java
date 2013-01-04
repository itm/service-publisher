package de.uniluebeck.itm.jettyservicesrunner;

import com.google.common.util.concurrent.Service;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.ws.rs.core.Application;

public interface JettyServicesRunner extends Service {

	void publishJaxWsEndpoint(String contextPath, Object endpointImpl);

	void publishJaxRsApplication(String contextPath, final Class<? extends Application> applicationClass);

	void publishWebSocketServlet(String contextPath, WebSocketServlet webSocketServlet);

}

package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.Service;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.ws.rs.core.Application;

public interface ServicePublisher extends Service {

	Service createJaxWsService(String contextPath, Object endpointImpl);

	Service createJaxRsService(String contextPath, Application application);

	Service createWebSocketService(String contextPath, WebSocketServlet webSocketServlet);

}

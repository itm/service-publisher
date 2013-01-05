package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.Service;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.ws.rs.core.Application;

public interface ServicePublisher extends Service {

	ServicePublisherJaxWsService createJaxWsService(String contextPath, Object endpointImpl);

	ServicePublisherJaxRsService createJaxRsService(String contextPath, Class<? extends Application> applicationClass);

	ServicePublisherWebSocketService createWebSocketService(String contextPath, WebSocketServlet webSocketServlet);

}

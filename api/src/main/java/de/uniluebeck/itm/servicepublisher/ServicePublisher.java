package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.Service;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.ws.rs.core.Application;

public interface ServicePublisher extends Service {

	ServicePublisherService createJaxWsService(String contextPath, Object endpointImpl);

	ServicePublisherService createJaxRsService(String contextPath, Application application);

	ServicePublisherService createWebSocketService(String contextPath, WebSocketServlet webSocketServlet);

}

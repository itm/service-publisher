package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.Service;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.annotation.Nullable;
import javax.ws.rs.core.Application;
import java.io.File;
import java.util.Map;

public interface ServicePublisher extends Service {

	ServicePublisherService createServletService(String contextPath, String resourceBase, @Nullable File shiroIni);

	ServicePublisherService createServletService(
			String contextPath,
			String resourceBase, @Nullable File shiroIni,
			@Nullable Map<String, String> initParams);

	ServicePublisherService createJaxWsService(String contextPath, Object endpointImpl, @Nullable File shiroIni);

	ServicePublisherService createJaxRsService(String contextPath, Application application, @Nullable File shiroIni);

	ServicePublisherService createWebSocketService(String contextPath, WebSocketServlet webSocketServlet,
												   @Nullable File shiroIni);

}

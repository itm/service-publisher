package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.Service;
import org.apache.shiro.config.Ini;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.annotation.Nullable;
import javax.servlet.Filter;
import javax.ws.rs.core.Application;
import java.util.Map;

public interface ServicePublisher extends Service {

	ServicePublisherService createServletService(String contextPath, String resourceBase, @Nullable Ini shiroIni);

	ServicePublisherService createServletService(
			String contextPath,
			String resourceBase,
			@Nullable Map<String, String> initParams,
			@Nullable Ini shiroIni,
			Filter... filters);

	ServicePublisherService createJaxWsService(String contextPath, Object endpointImpl, @Nullable Ini shiroIni);

	ServicePublisherService createJaxRsService(String contextPath, Application application, @Nullable Ini shiroIni);

	ServicePublisherService createWebSocketService(String contextPath, WebSocketServlet webSocketServlet);

}

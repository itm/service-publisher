package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.Service;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.annotation.Nullable;
import java.net.URI;

public interface ServicePublisherService extends Service {

	URI getURI();

	/**
	 * Not working for JAX-WS services yet.
	 *
	 * @return the handler or null in case of JAX-WS services
	 */
	@Nullable
	ServletContextHandler getServletContextHandler();

}

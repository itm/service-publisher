package de.uniluebeck.itm.servicepublisher;

import com.google.common.util.concurrent.Service;

import java.net.URI;

public interface ServicePublisherService extends Service {

	URI getURI();

}

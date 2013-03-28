package de.uniluebeck.itm.servicepublisher.demo;

import org.apache.cxf.jaxrs.provider.json.JSONProvider;

import javax.ws.rs.core.Application;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class DemoRestApplication2 extends Application {

	@Override
	public Set<Object> getSingletons() {

		final JSONProvider jsonProvider = new JSONProvider();
		jsonProvider.setDropRootElement(true);
		jsonProvider.setSupportUnwrapped(true);
		jsonProvider.setDropCollectionWrapperElement(true);
		jsonProvider.setSerializeAsArray(true);

		return newHashSet(new DemoRestService2Impl(), jsonProvider);
	}
}

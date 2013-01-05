package de.uniluebeck.itm.servicepublisher.demo;

import com.google.common.collect.Sets;

import javax.ws.rs.core.Application;
import java.util.Set;

public class DemoRestApplication2 extends Application {

	@Override
	public Set<Object> getSingletons() {
		return Sets.<Object>newHashSet(new DemoRestService2());
	}
}

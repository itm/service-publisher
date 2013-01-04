package de.uniluebeck.itm.jettyservicesrunner;

import com.google.common.collect.Sets;

import javax.ws.rs.core.Application;
import java.util.Set;

public class DemoRestApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		return Sets.<Object>newHashSet(new DemoRestService());
	}
}

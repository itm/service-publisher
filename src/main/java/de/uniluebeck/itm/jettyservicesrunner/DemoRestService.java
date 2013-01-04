package de.uniluebeck.itm.jettyservicesrunner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class DemoRestService {

	@GET
	public String get() {
		return "got me!";
	}

}

package de.uniluebeck.itm.servicepublisher.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class DemoRestService2 {

	@GET
	public DemoDto get() {
		return new DemoDto();
	}

}

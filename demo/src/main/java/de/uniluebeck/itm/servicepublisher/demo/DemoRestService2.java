package de.uniluebeck.itm.servicepublisher.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/")
public interface DemoRestService2 {

	@GET
	DemoDto get();

	@GET
	@Path("/list")
	List<DemoDto> getList();

}

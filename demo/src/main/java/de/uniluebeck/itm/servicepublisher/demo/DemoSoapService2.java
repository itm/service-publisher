package de.uniluebeck.itm.servicepublisher.demo;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class DemoSoapService2 {

	@WebMethod
	public DemoDto getDto() {
		return new DemoDto();
	}
}

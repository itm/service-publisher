package de.uniluebeck.itm.servicepublisher.demo;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class DemoSoapService {

	@WebMethod
	public int addNumbers(int a, int b) {
		return a + b;
	}
}

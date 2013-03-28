package de.uniluebeck.itm.servicepublisher.demo;

import de.uniluebeck.itm.tr.util.Logging;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class DemoClient {

	static {
		Logging.setLoggingDefaults();
	}

	public static void main(String[] args) {

		for (DemoDto demoDto : client().getList()) {
			System.out.println(demoDto);
		}
	}

	private static DemoRestService2 client() {

		final JSONProvider jsonProvider = new JSONProvider();
		jsonProvider.setDropRootElement(true);
		jsonProvider.setSupportUnwrapped(true);
		jsonProvider.setDropCollectionWrapperElement(true);
		jsonProvider.setSerializeAsArray(true);

		final List<JSONProvider> providers = newArrayList(jsonProvider);

		return JAXRSClientFactory.create("http://localhost:8080/rest/v2.0/", DemoRestService2.class, providers);
	}

}

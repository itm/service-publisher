package de.uniluebeck.itm.servicepublisher.cxf;

import org.apache.shiro.config.Ini;
import org.apache.shiro.web.env.IniWebEnvironment;

public class ServicePublisherEnvironment extends IniWebEnvironment {

	public ServicePublisherEnvironment(final Ini ini) {
		super();
		setIni(ini);
	}
}

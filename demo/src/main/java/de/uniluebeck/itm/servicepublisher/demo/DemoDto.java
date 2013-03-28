package de.uniluebeck.itm.servicepublisher.demo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DemoDto {

	public String name = "myName";

	@Override
	public String toString() {
		return "DemoDto{" +
				"name='" + name + '\'' +
				'}';
	}
}

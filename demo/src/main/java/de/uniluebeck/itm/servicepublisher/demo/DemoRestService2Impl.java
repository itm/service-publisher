package de.uniluebeck.itm.servicepublisher.demo;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class DemoRestService2Impl implements DemoRestService2 {

	@Override
	public DemoDto get() {
		return new DemoDto();
	}

	@Override
	public List<DemoDto> getList() {
		return newArrayList(new DemoDto());
	}

}

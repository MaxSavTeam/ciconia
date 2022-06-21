package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;

@Mapping
@Component
public class EmptyMappingController {

	@Mapping
	public String root(){
		return "root";
	}

	@Mapping("/hello")
	public String hello(){
		return "hello";
	}

}

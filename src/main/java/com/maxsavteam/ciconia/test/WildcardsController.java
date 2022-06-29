package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;
import spark.Request;

@Mapping
@Component
public class WildcardsController {

	@Mapping("/wildcards/test/**")
	public String test(
			Request request
	){
		return "/wildcards/test/**<br>" +
				request.pathInfo();
	}

	@Mapping("/wildcards/*")
	public String test1(
			Request request
	){
		return "/wildcards/*<br>" +
				request.pathInfo();
	}

	@Mapping("/wildcards/?/")
	public String test2(
			Request request
	){
		return "/wildcards/?/<br>" +
				request.pathInfo();
	}

}

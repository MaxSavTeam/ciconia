package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.sparkjava.annotation.Header;

@Mapping("header")
@Component
public class HeaderController {

	@Mapping("user-agent")
	public String getUserAgent(
			@Header("User-Agent") String userAgent
	){
		return userAgent;
	}

	@Mapping("accept-language")
	public String getAcceptLanguage(
			@Header("Accept-Language") String acceptLanguage
	){
		return acceptLanguage;
	}

}

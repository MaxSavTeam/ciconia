package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotation.Configuration;
import com.maxsavteam.ciconia.annotation.ObjectFactory;
import com.maxsavteam.ciconia.annotation.PostInitialization;

@Configuration
public class Config {

	@ObjectFactory
	public TestBean createBean(){
		TestBean testBean = new TestBean();
		System.out.println("Created bean " + testBean);
		return testBean;
	}

	@PostInitialization
	public void postInitialization(){
		System.out.println("Post initialization");
	}

}

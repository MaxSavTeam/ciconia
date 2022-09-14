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

	@PostInitialization(order = 1)
	public void postInitialization1(){
		System.out.println("Post initialization 1");
	}

	@PostInitialization(order = 2)
	public void postInitialization2(){
		System.out.println("Post initialization 2");
	}

}

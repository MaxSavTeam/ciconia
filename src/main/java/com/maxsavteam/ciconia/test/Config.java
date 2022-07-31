package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotation.Configuration;
import com.maxsavteam.ciconia.annotation.ObjectFactory;

@Configuration
public class Config {

	@ObjectFactory
	private TestBean createBean(){
		TestBean testBean = new TestBean();
		System.out.println("Created bean " + testBean);
		return testBean;
	}

}

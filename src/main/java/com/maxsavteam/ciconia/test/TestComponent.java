package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotations.Component;

@Component
public class TestComponent {

	public TestComponent(Main main){

	}

	public String test(String x){
		return "hello, " + x;
	}

}

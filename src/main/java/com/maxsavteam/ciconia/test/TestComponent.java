package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotations.Component;
import com.maxsavteam.ciconia.annotations.Mapping;
import com.maxsavteam.ciconia.annotations.RequestMethod;

@Mapping("test")
@Component
public class TestComponent {

	public TestComponent(Main main){

	}

	public String test(String x){
		return "hello, " + x;
	}

	@Mapping(value = "", method = RequestMethod.POST)
	public void test2(){
		System.out.println("hey");
	}

}

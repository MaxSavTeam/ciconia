package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.RequestMethod;

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

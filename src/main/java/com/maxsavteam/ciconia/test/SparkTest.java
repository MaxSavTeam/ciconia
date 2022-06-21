package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.PathVariable;
import com.maxsavteam.ciconia.sparkjava.CiconiaSparkApplication;
import com.maxsavteam.ciconia.sparkjava.CiconiaSparkConfiguration;

@Mapping
@Component
public class SparkTest {

	public static void main(String[] args) {
		CiconiaSparkApplication.run(SparkTest.class,
				new CiconiaSparkConfiguration.Builder()
						.setExceptionHandler((exception, request, response) -> {
							System.out.println(exception.toString());
							response.status(500);
							response.body(exception.toString());
						})
						.build()
		);
	}

	@Mapping("hello-{name}")
	public String hello(
			@PathVariable("name") String name
	){
		return "hello, " + name;
	}

}

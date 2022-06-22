package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.PathVariable;
import com.maxsavteam.ciconia.sparkjava.CiconiaSparkApplication;
import com.maxsavteam.ciconia.sparkjava.CiconiaSparkConfiguration;
import spark.Request;
import spark.Response;

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
	public void hello(
			@PathVariable("name") String name,
			TestComponent testComponent,
			Response response
	){
		response.type("text/plain");
		response.body(testComponent.test(name));
		response.status(200);
	}

}

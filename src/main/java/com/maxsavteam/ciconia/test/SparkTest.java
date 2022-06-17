package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.sparkjava.CiconiaSparkApplication;
import com.maxsavteam.ciconia.sparkjava.CiconiaSparkConfiguration;

public class SparkTest {

	public static void main(String[] args) {
		CiconiaSparkApplication.run(SparkTest.class,
				new CiconiaSparkConfiguration.Builder()
						.setExceptionHandler((exception, request, response) -> {
							System.out.println(exception.getMessage());
							response.status(500);
							response.body(exception.getMessage());
						})
						.build()
		);
	}

}

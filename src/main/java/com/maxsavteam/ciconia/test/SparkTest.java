package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.CiconiaConfiguration;
import com.maxsavteam.ciconia.sparkjava.CiconiaSparkApplication;

public class SparkTest {

	public static void main(String[] args) {
		CiconiaSparkApplication.run(SparkTest.class, new CiconiaConfiguration.Builder().build());
	}

}

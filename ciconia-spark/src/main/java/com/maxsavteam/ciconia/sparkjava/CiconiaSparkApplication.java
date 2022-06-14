package com.maxsavteam.ciconia.sparkjava;

import com.maxsavteam.ciconia.CiconiaApplication;
import com.maxsavteam.ciconia.CiconiaConfiguration;
import com.maxsavteam.ciconia.CiconiaHandler;
import com.maxsavteam.ciconia.annotations.RequestMethod;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Spark;

public class CiconiaSparkApplication {

	public static void run(Class<?> clazz, CiconiaConfiguration configuration){
		CiconiaConfiguration.Builder builder = new CiconiaConfiguration.Builder(configuration);
		builder.setPathSeparator('/');
		CiconiaApplication.run(clazz, builder.build());

		Spark.get("*", (request, response) -> handleRequest(request, response, RequestMethod.GET));
		Spark.post("*", (request, response) -> handleRequest(request, response, RequestMethod.POST));
	}

	private static String handleRequest(Request request, Response response, RequestMethod requestMethod){
		String path = request.pathInfo().substring(1);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("method", path);
		JSONObject params = new JSONObject();
		for(String attribute : request.queryParams())
			params.put(attribute, request.queryParams(attribute));
		jsonObject.put("params", params);

		Object result = CiconiaHandler.getInstance().handle(
				jsonObject,
				requestMethod
		);
		return result.toString();
	}

}

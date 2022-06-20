package com.maxsavteam.ciconia.sparkjava;

import com.maxsavteam.ciconia.CiconiaApplication;
import com.maxsavteam.ciconia.CiconiaHandler;
import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.exception.CiconiaRuntimeException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Spark;

public class CiconiaSparkApplication {

	private final CiconiaSparkConfiguration configuration;

	public CiconiaSparkApplication(CiconiaSparkConfiguration configuration) {
		this.configuration = configuration;
	}

	private void run(Class<?> clazz) {
		CiconiaApplication.run(clazz, configuration);

		Spark.get("*", (request, response) -> handleRequest(request, response, RequestMethod.GET));
		Spark.post("*", (request, response) -> handleRequest(request, response, RequestMethod.POST));

		if (configuration.getExceptionHandler() != null)
			Spark.exception(CiconiaRuntimeException.class, configuration.getExceptionHandler());
	}

	public static void run(Class<?> clazz, CiconiaSparkConfiguration configuration) {
		new CiconiaSparkApplication(configuration).run(clazz);
	}

	private Object handleRequest(Request request, Response response, RequestMethod requestMethod) {
		String path = request.pathInfo().substring(1);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("method", path);
		JSONObject params = new JSONObject();
		for (String attribute : request.queryParams())
			params.put(attribute, request.queryParams(attribute));
		jsonObject.put("params", params);

		Object result = CiconiaHandler.getInstance().handle(
				jsonObject,
				requestMethod
		);
		return result.toString();
	}

}

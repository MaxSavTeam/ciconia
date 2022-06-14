package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.CiconiaApplication;
import com.maxsavteam.ciconia.CiconiaConfiguration;
import com.maxsavteam.ciconia.CiconiaHandler;
import com.maxsavteam.ciconia.annotations.Component;
import com.maxsavteam.ciconia.annotations.Mapping;
import com.maxsavteam.ciconia.annotations.Param;
import com.maxsavteam.ciconia.annotations.RequestMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Component
@Mapping("test")
public class Main {

	public static void main(String[] args) {
		CiconiaConfiguration configuration = new CiconiaConfiguration.Builder()
				.setPathSeparator('/')
				.build();
		CiconiaApplication.run(Main.class, configuration);
		Object result = CiconiaHandler.getInstance().handle(
				new JSONObject()
						.put("method", "test/")
						.put("params", new JSONObject()
								.put("list", new JSONArray().put("hello").put("world"))
								.put("x", "Oleg")
						),
				RequestMethod.POST
		);
		System.out.println();
		System.out.println(result);
	}

	@Mapping("test")
	public void helloWorld(@Param("list") List<String> list){
		for(var i : list)
			System.out.println(i);
	}

	@Mapping(value = "test_", method = RequestMethod.GET)
	public String testComponent(TestComponent testComponent, @Param("x") String x, @Param("y") Integer y){
		return testComponent.test(x) + y;
	}

	@Mapping(value = "test_json", method = RequestMethod.GET)
	public String testJson(@Param("json") JSONObject jsonObject){
		return jsonObject.toString();
	}

}

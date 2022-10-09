package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.CiconiaApplication;
import com.maxsavteam.ciconia.CiconiaConfiguration;
import com.maxsavteam.ciconia.CiconiaHandler;
import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Cron;
import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.Param;
import com.maxsavteam.ciconia.annotation.RequestMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Mapping("test")
public class Main {

	@Cron("*/5 * * * * ?")
	public void cronTest(){
		System.out.println("Cron test: " + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date()));
	}

	public static void main(String[] args) {
		CiconiaConfiguration configuration = new CiconiaConfiguration.Builder()
				.setPathSeparator('/')
				.build();
		CiconiaApplication.run(Main.class, configuration);
		Object result = CiconiaHandler.getInstance().handle(
				new JSONObject()
						.put("method", "test/list")
						.put("params", new JSONObject()
								.put("list", new JSONArray().put("1").put("2"))
								.put("json", new JSONObject().put("name", "Max"))
								.put("x", "Oleg")
						),
				RequestMethod.GET
		);
		System.out.println();
		System.out.println(result);
	}

	public static class Test {
		public String name;
	}

	@Mapping("jackson")
	public void test(@Param("json") Test test){
		System.out.println(test.name);
	}

	@Mapping("list")
	public void helloWorld(@Param("list") List<Double> list){
		for(Double i : list)
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

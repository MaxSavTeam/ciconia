package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.annotation.Cron;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents particular component class.
 * @author Max Savitsky
 * */
public class Component extends InstantiatableComponent {

	private List<CronMethod> cronMethods = new ArrayList<>();

	public Component(Class<?> aClass) {
		super(aClass);
	}

	public void setCronMethods(List<CronMethod> cronMethods) {
		this.cronMethods = cronMethods;
	}

	public List<CronMethod> getCronMethods() {
		return cronMethods;
	}

	public static class CronMethod {

		private final Cron cron;
		private final Method method;

		public CronMethod(Cron cron, Method method) {
			this.cron = cron;
			this.method = method;
		}

		public Cron getCron() {
			return cron;
		}

		public Method getMethod() {
			return method;
		}
	}

}

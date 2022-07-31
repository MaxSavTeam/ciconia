package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.handler.ParameterAnnotationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CiconiaConfiguration {

	private final char pathSeparator;
	private final List<ParameterAnnotationHandler> parameterAnnotationHandlers;
	private final boolean handlerEnabled;

	protected CiconiaConfiguration(Builder builder) {
		this.pathSeparator = builder.pathSeparator;
		this.parameterAnnotationHandlers = Collections.unmodifiableList(builder.parameterAnnotationHandlers);
		this.handlerEnabled = builder.handlerEnabled;
	}

	public char getPathSeparator() {
		return pathSeparator;
	}

	public List<ParameterAnnotationHandler> getParameterAnnotationHandlers() {
		return parameterAnnotationHandlers;
	}

	public boolean isHandlerEnabled() {
		return handlerEnabled;
	}

	public static class Builder {

		protected char pathSeparator = '.';
		protected final List<ParameterAnnotationHandler> parameterAnnotationHandlers = new ArrayList<>();
		protected boolean handlerEnabled = true;

		public Builder() {}

		public Builder(CiconiaConfiguration configuration) {
			this.pathSeparator = configuration.pathSeparator;
		}

		public Builder setPathSeparator(char pathSeparator) {
			this.pathSeparator = pathSeparator;
			return this;
		}

		public Builder addParameterAnnotationHandler(ParameterAnnotationHandler handler) {
			this.parameterAnnotationHandlers.add(handler);
			return this;
		}

		public Builder setHandlerEnabled(boolean handlerEnabled) {
			this.handlerEnabled = handlerEnabled;
			return this;
		}

		public CiconiaConfiguration build() {
			return new CiconiaConfiguration(this);
		}

	}

}

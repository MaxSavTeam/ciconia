package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.handler.ParameterAnnotationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configures Ciconia application.
 * <br>
 * <a href="https://ciconia.maxsavteam.com/docs/#/configuration/ciconia-configuration-class">Documentation</a>
 * */
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

		/**
		 * Specifies character by which the mappings will be divided into sections.
		 * */
		public Builder setPathSeparator(char pathSeparator) {
			this.pathSeparator = pathSeparator;
			return this;
		}

		public Builder addParameterAnnotationHandler(ParameterAnnotationHandler handler) {
			this.parameterAnnotationHandlers.add(handler);
			return this;
		}

		/**
		 * If <code>false</code> {@link CiconiaHandler} will be disabled and will not be able to process requests.
		 * */
		public Builder setHandlerEnabled(boolean handlerEnabled) {
			this.handlerEnabled = handlerEnabled;
			return this;
		}

		public CiconiaConfiguration build() {
			return new CiconiaConfiguration(this);
		}

	}

}

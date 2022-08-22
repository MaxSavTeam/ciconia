package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.handler.ParameterAnnotationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configures Ciconia application.
 * @see <a href="https://ciconia.maxsavteam.com/docs/#/configuration/ciconia-configuration-class">Documentation</a>
 * @author Max Savitsky
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
		 * @param pathSeparator Character by which the mappings will be divided into sections.
		 * @return this builder
		 * */
		public Builder setPathSeparator(char pathSeparator) {
			this.pathSeparator = pathSeparator;
			return this;
		}

		/**
		 * Adds parameter annotation handler to the list of handlers.
		 * @param handler Parameter annotation handler to add.
		 * @return this builder
		 * */
		public Builder addParameterAnnotationHandler(ParameterAnnotationHandler handler) {
			this.parameterAnnotationHandlers.add(handler);
			return this;
		}

		/**
		 * If <code>false</code> {@link CiconiaHandler} will be disabled and will not be able to process requests.
		 * @param handlerEnabled {@code false} to disable {@link CiconiaHandler}.
		 * @return this builder
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

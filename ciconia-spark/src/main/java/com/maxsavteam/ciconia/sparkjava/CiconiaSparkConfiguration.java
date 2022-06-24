package com.maxsavteam.ciconia.sparkjava;

import com.maxsavteam.ciconia.CiconiaConfiguration;
import com.maxsavteam.ciconia.annotation.handler.ParameterAnnotationHandler;
import com.maxsavteam.ciconia.sparkjava.annotation.handler.HeaderHandler;
import spark.Spark;

public class CiconiaSparkConfiguration extends CiconiaConfiguration {

	private final CiconiaExceptionHandler exceptionHandler;

	private final int port;

	protected CiconiaSparkConfiguration(Builder builder) {
		super(builder);
		this.exceptionHandler = builder.exceptionHandler;
		this.port = builder.port;
	}

	public CiconiaExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public int getPort() {
		return port;
	}

	public static class Builder extends CiconiaConfiguration.Builder {

		private CiconiaExceptionHandler exceptionHandler;

		private int port = 4567;

		public Builder setExceptionHandler(CiconiaExceptionHandler exceptionHandler) {
			this.exceptionHandler = exceptionHandler;
			return this;
		}

		@Override
		public Builder setPathSeparator(char pathSeparator) {
			throw new UnsupportedOperationException("Path separator cannot be changed");
		}

		public Builder setPort(int port) {
			this.port = port;
			return this;
		}

		@Override
		public Builder addParameterAnnotationHandler(ParameterAnnotationHandler handler) {
			super.addParameterAnnotationHandler(handler);
			return this;
		}

		@Override
		public CiconiaSparkConfiguration build() {
			pathSeparator = '/';
			addParameterAnnotationHandler(new HeaderHandler());
			return new CiconiaSparkConfiguration(this);
		}
	}

}

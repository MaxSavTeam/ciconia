package com.maxsavteam.ciconia.sparkjava;

import com.maxsavteam.ciconia.CiconiaConfiguration;

public class CiconiaSparkConfiguration extends CiconiaConfiguration {

	private final CiconiaExceptionHandler exceptionHandler;

	protected CiconiaSparkConfiguration(Builder builder) {
		super(builder);
		this.exceptionHandler = builder.exceptionHandler;
	}

	public CiconiaExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public static class Builder extends CiconiaConfiguration.Builder {

		private CiconiaExceptionHandler exceptionHandler;

		public Builder setExceptionHandler(CiconiaExceptionHandler exceptionHandler) {
			this.exceptionHandler = exceptionHandler;
			return this;
		}

		@Override
		public Builder setPathSeparator(char pathSeparator) {
			throw new UnsupportedOperationException("Path separator cannot be changed");
		}

		@Override
		public CiconiaSparkConfiguration build() {
			pathSeparator = '/';
			return new CiconiaSparkConfiguration(this);
		}
	}

}

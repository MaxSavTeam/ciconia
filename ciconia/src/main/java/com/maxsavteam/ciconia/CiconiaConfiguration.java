package com.maxsavteam.ciconia;

public class CiconiaConfiguration {

	private final char pathSeparator;

	protected CiconiaConfiguration(Builder builder) {
		this.pathSeparator = builder.pathSeparator;
	}

	public char getPathSeparator() {
		return pathSeparator;
	}

	public static class Builder {

		protected char pathSeparator = '.';

		public Builder() {}

		public Builder(CiconiaConfiguration configuration) {
			this.pathSeparator = configuration.pathSeparator;
		}

		public Builder setPathSeparator(char pathSeparator) {
			this.pathSeparator = pathSeparator;
			return this;
		}

		public CiconiaConfiguration build() {
			return new CiconiaConfiguration(this);
		}

	}

}

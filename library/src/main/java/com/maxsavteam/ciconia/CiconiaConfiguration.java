package com.maxsavteam.ciconia;

public class CiconiaConfiguration {

	private final char pathSeparator;

	private CiconiaConfiguration(Builder builder) {
		this.pathSeparator = builder.pathSeparator;
	}

	public char getPathSeparator() {
		return pathSeparator;
	}

	public static class Builder {

		private char pathSeparator = '.';

		public Builder setPathSeparator(char pathSeparator) {
			this.pathSeparator = pathSeparator;
			return this;
		}

		public CiconiaConfiguration build() {
			return new CiconiaConfiguration(this);
		}

	}

}

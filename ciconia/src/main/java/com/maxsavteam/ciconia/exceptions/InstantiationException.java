package com.maxsavteam.ciconia.exceptions;

public class InstantiationException extends RuntimeException {

	public InstantiationException() {
	}

	public InstantiationException(String message) {
		super(message);
	}

	public InstantiationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InstantiationException(Throwable cause) {
		super(cause);
	}
}

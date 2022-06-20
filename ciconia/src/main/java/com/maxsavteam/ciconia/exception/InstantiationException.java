package com.maxsavteam.ciconia.exception;

public class InstantiationException extends CiconiaRuntimeException {

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

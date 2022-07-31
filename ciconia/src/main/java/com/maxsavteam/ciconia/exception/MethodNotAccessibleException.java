package com.maxsavteam.ciconia.exception;

public class MethodNotAccessibleException extends CiconiaRuntimeException {

	public MethodNotAccessibleException() {
	}

	public MethodNotAccessibleException(String message) {
		super(message);
	}

	public MethodNotAccessibleException(String message, Throwable cause) {
		super(message, cause);
	}

	public MethodNotAccessibleException(Throwable cause) {
		super(cause);
	}
}

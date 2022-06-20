package com.maxsavteam.ciconia.exception;

public class CiconiaRuntimeException extends RuntimeException {

	public CiconiaRuntimeException() {
		super();
	}

	public CiconiaRuntimeException(String message) {
		super(message);
	}

	public CiconiaRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public CiconiaRuntimeException(Throwable cause) {
		super(cause);
	}

	protected CiconiaRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

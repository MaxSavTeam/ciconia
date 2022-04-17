package com.maxsavteam.ciconia.exceptions;

public class DuplicateMappingException extends RuntimeException {

	public DuplicateMappingException() {
	}

	public DuplicateMappingException(String message) {
		super(message);
	}

	public DuplicateMappingException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateMappingException(Throwable cause) {
		super(cause);
	}
}

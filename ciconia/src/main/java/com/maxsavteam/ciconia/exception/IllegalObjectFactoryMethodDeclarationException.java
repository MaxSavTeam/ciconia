package com.maxsavteam.ciconia.exception;

public class IllegalObjectFactoryMethodDeclarationException extends CiconiaRuntimeException{

	public IllegalObjectFactoryMethodDeclarationException() {
	}

	public IllegalObjectFactoryMethodDeclarationException(String message) {
		super(message);
	}

	public IllegalObjectFactoryMethodDeclarationException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalObjectFactoryMethodDeclarationException(Throwable cause) {
		super(cause);
	}
}

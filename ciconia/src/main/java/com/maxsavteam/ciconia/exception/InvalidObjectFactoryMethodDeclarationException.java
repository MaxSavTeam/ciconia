package com.maxsavteam.ciconia.exception;

public class InvalidObjectFactoryMethodDeclarationException extends InvalidMethodDeclaration {

	public InvalidObjectFactoryMethodDeclarationException() {
	}

	public InvalidObjectFactoryMethodDeclarationException(String message) {
		super(message);
	}

	public InvalidObjectFactoryMethodDeclarationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidObjectFactoryMethodDeclarationException(Throwable cause) {
		super(cause);
	}
}

package com.maxsavteam.ciconia.exception;

public class InvalidMethodDeclaration extends CiconiaRuntimeException{

	public InvalidMethodDeclaration() {
	}

	public InvalidMethodDeclaration(String message) {
		super(message);
	}

	public InvalidMethodDeclaration(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidMethodDeclaration(Throwable cause) {
		super(cause);
	}

}

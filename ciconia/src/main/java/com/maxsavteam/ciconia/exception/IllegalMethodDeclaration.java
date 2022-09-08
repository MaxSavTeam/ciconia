package com.maxsavteam.ciconia.exception;

public class IllegalMethodDeclaration extends CiconiaRuntimeException{

	public IllegalMethodDeclaration() {
	}

	public IllegalMethodDeclaration(String message) {
		super(message);
	}

	public IllegalMethodDeclaration(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalMethodDeclaration(Throwable cause) {
		super(cause);
	}

}

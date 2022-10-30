package com.maxsavteam.ciconia.exception;

public class InvalidFieldDeclaration extends CiconiaRuntimeException{

	public InvalidFieldDeclaration() {
	}

	public InvalidFieldDeclaration(String message) {
		super(message);
	}

	public InvalidFieldDeclaration(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidFieldDeclaration(Throwable cause) {
		super(cause);
	}

}

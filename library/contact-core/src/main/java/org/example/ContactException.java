package org.example;

public class ContactException extends RuntimeException {

	public ContactException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContactException(String message) {
		super(message);
	}
}

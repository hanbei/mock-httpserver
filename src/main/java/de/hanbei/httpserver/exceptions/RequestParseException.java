package de.hanbei.httpserver.exceptions;

public class RequestParseException extends RuntimeException {

	private static final long serialVersionUID = -8933346579711421055L;

	public RequestParseException(String message) {
		super(message);
	}

	public RequestParseException(String message, Throwable throwable) {
		super(message, throwable);
	}


}

package de.hanbei.httpserver.exceptions;

/** @author fschulz */
public class ServerErrorException extends RuntimeException {

	private static final long serialVersionUID = 1916736246817511574L;

	public ServerErrorException(String message) {
		super(message);
	}

	public ServerErrorException(String message, Throwable throwable) {
		super(message, throwable);
	}
}

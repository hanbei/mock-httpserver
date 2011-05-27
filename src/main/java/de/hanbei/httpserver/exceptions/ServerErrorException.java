package de.hanbei.httpserver.exceptions;

/** @author fschulz */
public class ServerErrorException extends RuntimeException {

    public ServerErrorException(String message) {
        super(message);
    }

    public ServerErrorException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

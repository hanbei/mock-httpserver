package de.hanbei.httpserver.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: fschulz
 * Date: 03.06.11
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public class ContentException extends RuntimeException {

    public ContentException(Throwable throwable) {
        super(throwable);
    }
}

package de.hanbei.httpserver.handler;

import de.hanbei.httpserver.common.Status;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 26.02.11
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class MockHandler implements HttpHandler {

    private int handlerCalled = 0;

    public int getHandlerCalled() {
        return handlerCalled;
    }

    public Response handleRequest(Request request) {
        handlerCalled++;
        Response response = new Response().ok().build();
        return response;
    }
}

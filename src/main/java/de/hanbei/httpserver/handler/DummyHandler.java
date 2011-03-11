package de.hanbei.httpserver.handler;

import de.hanbei.httpserver.common.Status;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 26.02.11
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
public class DummyHandler implements  HttpHandler {
    public Response handleRequest(Request request) {
        Response response = Response.status(Status.CREATED).build();
        return response;
    }
}

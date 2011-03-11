package de.hanbei.httpserver.handler;

import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 26.02.11
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public interface HttpHandler {
	
    Response handleRequest(Request request);
}

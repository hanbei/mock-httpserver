package de.hanbei.httpserver.handler;

import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 04.03.11
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class PredefinedResponseHandler implements HttpHandler {

    private Map<URI, Response> predefinedResponses;
    private Response defaultResponse;

    public PredefinedResponseHandler() {
        predefinedResponses = new HashMap<URI, Response>();
    }

    public Response getDefaultResponse() {
        return defaultResponse;
    }

    public void setDefaultResponse(Response defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    public Response handleRequest(Request request) {
        URI requestUri = request.getRequestUri();
        Response response = predefinedResponses.get(requestUri);
        if (response == null) {
            response = defaultResponse;
        }
        return response;
    }

    public void addResponse(URI uri, Response response) {
        predefinedResponses.put(uri, response);
    }
}

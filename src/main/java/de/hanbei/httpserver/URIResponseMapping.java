package de.hanbei.httpserver;

import de.hanbei.httpserver.response.Response;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 31.03.11
 * Time: 20:21
 * To change this template use File | Settings | File Templates.
 */
class URIResponseMapping {

    private Map<URI, Response> responses;

    public URIResponseMapping() {
        responses = new HashMap<URI, Response>();
    }

    public Response getResponse(URI requestUri) {
        return responses.get(requestUri);
    }

    public void addResponse(URI uri, Response response) {
        responses.put(uri, response);
    }
}

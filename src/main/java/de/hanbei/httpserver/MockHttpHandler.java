package de.hanbei.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.hanbei.httpserver.common.Method;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;
import de.hanbei.httpserver.response.ResponseWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hanbei on 10/12/14.
 */
public class MockHttpHandler implements HttpHandler {

    private Map<Method, Mapping<Response>> predefinedResponses;

    private Response defaultResponse;
    private boolean timeout;

    public MockHttpHandler() {
        predefinedResponses = new HashMap<Method, Mapping<Response>>();
        defaultResponse = Response.notFound().build();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Method method = Method.valueOf(httpExchange.getRequestMethod());
        URI requestURI = httpExchange.getRequestURI();

        Response response = getResponse(requestURI, method);
        try {
            if (!timeout) {
                sendHeaders(httpExchange, response);
                sendContent(httpExchange, response);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void sendContent(HttpExchange httpExchange, Response defaultResponse) throws IOException {
        httpExchange.getResponseBody().write(defaultResponse.getContent().getContent());
    }

    private void sendHeaders(HttpExchange httpExchange, Response response) throws IOException {
        httpExchange.sendResponseHeaders(response.getStatus().getStatusCode(), response.getContent().getLength());
    }

    private Response getResponse(URI requestUri1, Method method) {
        String requestUri = trimSlashes(requestUri1);
        Mapping<Response> mapping = predefinedResponses.get(method);
        if (mapping == null) {
            return defaultResponse;
        }
        Response response = mapping.get(requestUri);
        if (response == null) {
            return defaultResponse;
        }
        return response;
    }

    public Response getDefaultResponse() {
        return defaultResponse;
    }

    public void setDefaultResponse(Response defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    private String trimSlashes(URI uri) {
        String uriString = uri.toString();
        return uriString.replaceAll("^/|/$", "");
    }

    /**
     * Add a specified response for a certain url and method. The uri has to be relative to the server url.
     *
     * @param method   The method the response should be sent on.
     * @param uri      The uri the request will access on. Has to be relative to the server url.
     * @param response The response that should be sent on a request on the specified uri with the specified method.
     */
    public void addResponse(Method method, URI uri, Response response) {
        Mapping<Response> mapping = predefinedResponses.get(method);
        if (mapping == null) {
            mapping = new Mapping<Response>();
            predefinedResponses.put(method, mapping);
        }
        mapping.add(trimSlashes(uri), response);
    }

    private RequestProcessor getProcessor(Request request) {
//        String requestUri = trimSlashes(request.getRequestUri());
//        Mapping<RequestProcessor> mapping = requestProcessorMapping.get(request.getMethod());
//        RequestProcessor processor = null;
//        if (mapping != null) {
//            processor = mapping.get(requestUri);
//        }
//        return processor;
        return null;
    }

    private void sendResponse(Response response, Request request, Socket clientSocket) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();

        ResponseWriter responseWriter = new ResponseWriter(outputStream, request);
        responseWriter.write(response);
        responseWriter.close();
    }

    /**
     * Set if the server should timeout on requests.
     *
     * @param shouldTimeout true if the server should timeout, false otherwise.
     */
    public void setTimeout(boolean shouldTimeout) {
        timeout = shouldTimeout;
    }

    /**
     * Is the server set to timeout on requests.
     *
     * @return True if the server is set to timeout.
     */
    public boolean isTimeoutSet() {
        return timeout;
    }
}

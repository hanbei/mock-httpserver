package de.hanbei.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.common.Method;
import de.hanbei.httpserver.exceptions.ServerErrorException;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;

class MockHttpHandler implements HttpHandler {

    private Map<Method, Mapping<Response>> predefinedResponses;
    private Map<Method, Mapping<RequestProcessor>> requestProcessorMapping;

    private Response defaultResponse;
    private boolean timeout;

    public MockHttpHandler() {
        requestProcessorMapping = new HashMap<Method, Mapping<RequestProcessor>>();
        predefinedResponses = new HashMap<Method, Mapping<Response>>();
        defaultResponse = Response.notFound().build();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Method method = Method.valueOf(httpExchange.getRequestMethod());
        URI requestURI = httpExchange.getRequestURI();

        Response response = getResponse(requestURI, method);

        RequestProcessor processor = getProcessor(method, requestURI);

        if (processor != null) {
            try {
                Request request = portRequest(httpExchange);
                response = processor.process(request);
            } catch (Exception e) {
                throw new ServerErrorException("Error in porting requests", e);
            }
        }

        try {
            if (!timeout) {
                sendHeaders(httpExchange, response);
                sendContent(httpExchange, response);
            }
        } catch (Exception e) {
            throw new ServerErrorException("Error sending the response", e);
        } finally {
            httpExchange.close();
        }
    }

    private Request portRequest(HttpExchange httpExchange) throws IOException {
        Request request = new Request();

        Headers requestHeaders = httpExchange.getRequestHeaders();
        Header header = request.getHeader();
        for (String key : requestHeaders.keySet()) {
            for (String value : requestHeaders.get(key)) {
                header.addParameter(key, value);
            }
        }

        String encoding = requestHeaders.getFirst(Header.Fields.CONTENT_ENCODING);
        if (encoding != null) {
            request.getContent().setEncoding(encoding);
        }
        request.getContent().setLanguage(requestHeaders.getFirst(Header.Fields.CONTENT_LANGUAGE));
        request.getContent().setMd5(requestHeaders.getFirst(Header.Fields.CONTENT_MD5));
        String contentType = requestHeaders.getFirst(Header.Fields.CONTENT_TYPE);
        if (contentType != null) {
            if (encoding != null) {
                request.getContent().setMimetype(contentType + ";" + encoding);
            } else {
                request.getContent().setMimetype(contentType);
            }
        }
        String contentRange = requestHeaders.getFirst(Header.Fields.CONTENT_RANGE);
        if (contentRange != null) {
            request.getContent().setRange(contentRange);
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        IOUtils.copy(httpExchange.getRequestBody(), bytes);
        request.getContent().setContent(bytes.toByteArray());

        return request;
    }

    private void sendContent(HttpExchange httpExchange, Response defaultResponse) throws IOException {
        httpExchange.getResponseBody().write(defaultResponse.getContent().getContent());
    }

    private void sendHeaders(HttpExchange httpExchange, Response response) throws IOException {
        int length = response.getContent().getLength();
        int statusCode = response.getStatus().getStatusCode();

        Headers responseHeaders = httpExchange.getResponseHeaders();
        Header headers = response.getHeader();
        for (String headerField : headers.getHeaderFields()) {
            for (Header.Parameter value : headers.getHeaderParameter(headerField)) {
                responseHeaders.add(headerField, value.toString());
            }
        }
        addHeaderIfSet(responseHeaders, Header.Fields.CONTENT_ENCODING, response.getContent().getEncoding());
        addHeaderIfSet(responseHeaders, Header.Fields.CONTENT_LANGUAGE, response.getContent().getLanguage());
        addHeaderIfSet(responseHeaders, Header.Fields.CONTENT_MD5, response.getContent().getMd5());
        addHeaderIfSet(responseHeaders, Header.Fields.CONTENT_TYPE, response.getContent().getComposedContentType());
        addHeaderIfSet(responseHeaders, Header.Fields.CONTENT_RANGE, response.getContent().getRange());
        httpExchange.sendResponseHeaders(statusCode, max(0, length));
    }

    private void addHeaderIfSet(Headers responseHeaders, String name, String value) {
        if(value != null) {
            responseHeaders.add(name, value);
        }
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

    private RequestProcessor getProcessor(Method method, URI requestUriP) {
        String requestUri = trimSlashes(requestUriP);
        Mapping<RequestProcessor> mapping = requestProcessorMapping.get(method);
        RequestProcessor processor = null;
        if (mapping != null) {
            processor = mapping.get(requestUri);
        }
        return processor;
    }

    private String trimSlashes(URI uri) {
        String uriString = uri.toString();
        return uriString.replaceAll("^/|/$", "");
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

    /**
     * Add a {@link de.hanbei.httpserver.RequestProcessor} for a certain url and a method. It should make sense only POST and PUT requests as these are the only
     * ones that have a request body.
     *
     * @param method    The method the response should be sent on.
     * @param uri       The uri the request will access on. Has to be relative to the server url.
     * @param processor The {@link RequestProcessor} that processes the request body.
     */
    public void addRequestProcessor(Method method, URI uri, RequestProcessor processor) {
        Mapping<RequestProcessor> mapping = requestProcessorMapping.get(method);
        if (mapping == null) {
            mapping = new Mapping<RequestProcessor>();
            requestProcessorMapping.put(method, mapping);
        }
        mapping.add(trimSlashes(uri), processor);
    }
}

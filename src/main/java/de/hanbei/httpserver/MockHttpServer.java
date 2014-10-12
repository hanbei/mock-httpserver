/* Copyright 2011 Florian Schulz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package de.hanbei.httpserver;

import com.sun.net.httpserver.HttpServer;
import de.hanbei.httpserver.common.Method;
import de.hanbei.httpserver.common.Status;
import de.hanbei.httpserver.exceptions.ServerErrorException;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MockHttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockHttpServer.class);
    private final int port;
    MockHttpHandler httpHandler;
    private HttpServer server;
    private Map<Method, Mapping<RequestProcessor>> requestProcessorMapping;
    private boolean running;
    private boolean timeout;

    public MockHttpServer() {
        this(80);
    }

    /**
     * Create a new MockHttpServer running on a specific port.
     *
     * @param port The port the server should run on.
     */
    public MockHttpServer(int port) {
        this.port = port;
        timeout = false;
        running = false;

        requestProcessorMapping = new HashMap<Method, Mapping<RequestProcessor>>();

        try {
            httpHandler = new MockHttpHandler();
            server = HttpServer.create(new InetSocketAddress(this.port), 100);
            server.createContext("/", httpHandler);
        } catch (IOException e) {
            throw new ServerErrorException("Could not start the server.", e);
        }
    }

    public static void main(String[] args) throws IOException {
        MockHttpServer server = new MockHttpServer(7001);
        server.addRequestProcessor(Method.POST, URI.create("post"), new RequestProcessor() {
            @Override
            public Response process(Request request) {
                if ("Test".equals(request.getContent().getContentAsString())) {
                    return Response.ok().build();
                } else {
                    return Response.status(Status.UNAUTHORIZED).build();
                }
            }
        });
        server.addResponse(Method.GET, URI.create("test"), Response.notFound().build());
        server.start();
        System.in.read();
        server.stop();
    }

    /**
     * Start the server.
     */
    public void start() {
        server.start();
        running = true;
    }

    /**
     * Stop the server.
     */
    public void stop() {
        server.stop(1);
        running = false;
    }

    public boolean isTimeoutSet() {
        return httpHandler.isTimeoutSet();
    }

    public void setTimeout(boolean shouldTimeout) {
        httpHandler.setTimeout(shouldTimeout);
    }

    /**
     * Get the port the server is running on.
     *
     * @return The port the server is running on.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Is the server already running.
     *
     * @return true if the server is already running.
     */
    public boolean isRunning() {
        return running;
    }


    /**
     * Get the default response the server should send on any request that is not specified via {@link
     *
     * @return The default response.
     */
    public Response getDefaultResponse() {
        return httpHandler.getDefaultResponse();
    }

    /**
     * Set the default response for any request that has no specified response.
     *
     * @param defaultResponse The default response.
     */
    public void setDefaultResponse(Response defaultResponse) {
        this.httpHandler.setDefaultResponse(defaultResponse);
    }

    /**
     * Add a specified response for a certain url and the GET method. The uri has to be relative to the server url.
     *
     * @param uri      The uri the request will access on. Has to be relative to the server url.
     * @param response The response that should be sent on a request on the specified uri with the specified method.
     */
    public void addResponse(URI uri, Response response) {
        httpHandler.addResponse(Method.GET, uri, response);
    }

    public void addResponse(Method method, URI uri, Response response) {
        httpHandler.addResponse(method, uri, response);
    }

    /**
     * Add a {@link RequestProcessor} for a certain url and a method. It should make sense only POST and PUT requests as these are the only
     * ones that have a request body.
     *
     * @param method    The method the response should be sent on.
     * @param uri       The uri the request will access on. Has to be relative to the server url.
     * @param processor The {@link RequestProcessor} that processes the request body.
     */
    public void addRequestProcessor(Method method, URI uri, RequestProcessor processor) {
        Mapping<RequestProcessor> mapping = this.requestProcessorMapping.get(method);
        if (mapping == null) {
            mapping = new Mapping<RequestProcessor>();
            requestProcessorMapping.put(method, mapping);
        }
        mapping.add(trimSlashes(uri), processor);
    }

    private String trimSlashes(URI uri) {
        String uriString = uri.toString();
        return uriString.replaceAll("^/|/$", "");
    }

}

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

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hanbei.httpserver.common.Method;
import de.hanbei.httpserver.common.Status;
import de.hanbei.httpserver.exceptions.ServerErrorException;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.request.RequestParser;
import de.hanbei.httpserver.response.Response;
import de.hanbei.httpserver.response.ResponseWriter;

public class MockHttpServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockHttpServer.class);

    private ServerSocket serverSocket;
    private int port;

    private Map<Method, Mapping<Response>> predefinedResponses;
    private Map<Method, Mapping<RequestProcessor>> requestProcessorMapping;

    private Response defaultResponse;

    private boolean stopped;
    private boolean isStopping;

    private boolean timeout;

    private CountDownLatch startLatch;
    private CountDownLatch stopLatch;

    public MockHttpServer() {
        this(80);
    }

    /**
     * Create a new MockHttpServer running on a specific port.
     *
     * @param port The port the server should run on.
     */
    public MockHttpServer(int port) {
        startLatch = new CountDownLatch(1);
        stopLatch = new CountDownLatch(1);

        this.port = port;
        timeout = false;

        stopped = true;
        isStopping = false;

        predefinedResponses = new HashMap<Method, Mapping<Response>>();
        requestProcessorMapping = new HashMap<Method, Mapping<RequestProcessor>>();
        defaultResponse = Response.notFound().build();
    }

    /**
     * Start the server.
     */
    public void start() {
        isStopping = false;
        Thread listenerThread = new Thread(this, "MockHttpServer");
        listenerThread.start();

        try {
            startLatch.await();
        } catch ( InterruptedException e ) {
            stop();
            LOGGER.error("Error during startup", e);
        }
    }

    /**
     * Stop the server.
     */
    public void stop() {
        if ( !isRunning() ) {
            return;
        }
        isStopping = true;
        try {
            if ( this.serverSocket == null || this.serverSocket.isClosed() ) {
                return;
            }
            InetAddress serverAddress = this.serverSocket.getInetAddress();
            Socket socket = new Socket(serverAddress, this.port);
            socket.close();

            stopLatch.await();
        } catch ( ConnectException e ) {
            LOGGER.info("Could not send close request. But server socket is not waiting, just closing it.");
        } catch ( IOException e ) {
            LOGGER.warn("Error while stopping server", e);
        } catch ( InterruptedException e ) {
            LOGGER.warn("Error while stopping server", e);
        } finally {
            IOUtils.closeQuietly(serverSocket);
        }
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
     * Get the port the server is running on.
     *
     * @return The port the server is running on.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Set the port the server should run on.
     *
     * @param port The port the server is running on.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Is the server already running.
     *
     * @return true if the server is already running.
     */
    public boolean isRunning() {
        return !this.stopped;
    }

    /**
     * Implementation of the socket listening thread.
     */
    public void run() {
        try {
            this.serverSocket = new ServerSocket(MockHttpServer.this.port);
            LOGGER.info("Started server on {}:{}",
                MockHttpServer.this.serverSocket.getInetAddress(),
                MockHttpServer.this.serverSocket.getLocalPort());
            this.stopped = false;

            startLatch.countDown();

            while ( !MockHttpServer.this.isStopping ) {
                Socket clientSocket = MockHttpServer.this.serverSocket.accept();
                handle(clientSocket);
            }
        } catch ( IOException e ) {
            throw new ServerErrorException("Something went wrong during reading from the socket.", e);
        } finally {
            if ( serverSocket != null ) {
                IOUtils.closeQuietly(this.serverSocket);
            }
            this.stopped = true;
            stopLatch.countDown();
        }
    }

    private void handle(Socket clientSocket) {
        try {
            if ( !shouldHandleRequest(clientSocket) ) {
                return;
            }

            Request request = readRequest(clientSocket);

            if ( request == null || request.isEmpty() ) {
                LOGGER.warn("No Request");
                return;
            }

            if ( timeout ) {
                return;
            }

            RequestProcessor processor = getProcessor(request);
            Response response;
            if ( processor != null ) {
                response = processor.process(request);
            } else {
                response = getResponse(request);
            }

            sendResponse(response, request, clientSocket);
        } catch ( IOException e ) {
            LOGGER.warn("", e);
        } finally {
            try {
                clientSocket.close();
            } catch ( IOException e ) {
                LOGGER.warn("", e);
            }
        }
    }

    private boolean shouldHandleRequest(Socket clientSocket) {
        if ( isStopping ) {
            return false;
        }
        if ( clientSocket.isClosed() ) {
            return false;
        }
        return true;
    }

    private RequestProcessor getProcessor(Request request) {
        String requestUri = trimSlashes(request.getRequestUri());
        Mapping<RequestProcessor> mapping = requestProcessorMapping.get(request.getMethod());
        RequestProcessor processor = null;
        if ( mapping != null ) {
            processor = mapping.get(requestUri);
        }
        return processor;
    }

    private Response getResponse(Request request) {
        String requestUri = trimSlashes(request.getRequestUri());
        Mapping<Response> mapping = predefinedResponses.get(request.getMethod());
        if ( mapping == null ) {
            return defaultResponse;
        }
        Response response = mapping.get(requestUri);
        if ( response == null ) {
            return defaultResponse;
        }
        return response;
    }

    private Request readRequest(Socket clientSocket) throws IOException {
        try {
            Thread.sleep(100);
        } catch ( InterruptedException e ) {
            return null;
        }
        RequestParser requestParser = new RequestParser();
        return requestParser.parse(clientSocket.getInputStream());
    }

    private void sendResponse(Response response, Request request, Socket clientSocket) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();

        ResponseWriter responseWriter = new ResponseWriter(outputStream, request);
        responseWriter.write(response);
        responseWriter.close();
    }

    /**
     * Get the default response the server should send on any request that is not specified via {@link
     * MockHttpServer#addResponse(de.hanbei.httpserver.common.Method, java.net.URI, de.hanbei.httpserver.response.Response)}.
     *
     * @return The default response.
     * @see MockHttpServer#addResponse(de.hanbei.httpserver.common.Method, java.net.URI, de.hanbei.httpserver.response.Response)
     */
    public Response getDefaultResponse() {
        return defaultResponse;
    }

    /**
     * Set the default response for any request that has no specified response.
     *
     * @param defaultResponse The default response.
     */
    public void setDefaultResponse(Response defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    /**
     * Add a specified response for a certain url and the GET method. The uri has to be relative to the server url.
     *
     * @param uri The uri the request will access on. Has to be relative to the server url.
     * @param response The response that should be sent on a request on the specified uri with the specified method.
     */
    public void addResponse(URI uri, Response response) {
        addResponse(Method.GET, uri, response);
    }

    /**
     * Add a specified response for a certain url and method. The uri has to be relative to the server url.
     *
     * @param method The method the response should be sent on.
     * @param uri The uri the request will access on. Has to be relative to the server url.
     * @param response The response that should be sent on a request on the specified uri with the specified method.
     */
    public void addResponse(Method method, URI uri, Response response) {
        Mapping<Response> mapping = predefinedResponses.get(method);
        if ( mapping == null ) {
            mapping = new Mapping<Response>();
            predefinedResponses.put(method, mapping);
        }
        mapping.add(trimSlashes(uri), response);
    }

    /**
     * Add a {@link RequestProcessor} for a certain url and a method. It should make sense only POST and PUT requests as these are the only
     * ones that have a request body.
     *
     * @param method The method the response should be sent on.
     * @param uri The uri the request will access on. Has to be relative to the server url.
     * @param processor The {@link RequestProcessor} that processes the request body.
     */
    public void addRequestProcessor(Method method, URI uri, RequestProcessor processor) {
        Mapping<RequestProcessor> mapping = this.requestProcessorMapping.get(method);
        if ( mapping == null ) {
            mapping = new Mapping<RequestProcessor>();
            requestProcessorMapping.put(method, mapping);
        }
        mapping.add(trimSlashes(uri), processor);
    }

    private String trimSlashes(URI uri) {
        String uriString = uri.toString();
        return uriString.replaceAll("^/|/$", "");
    }

    public static void main(String[] args) throws IOException {
        MockHttpServer server = new MockHttpServer();
        server.addRequestProcessor(Method.POST, URI.create("post"), new RequestProcessor() {
            @Override
            public Response process(Request request) {
                if ( "Test".equals(request.getContent().getContentAsString()) ) {
                    return Response.ok().build();
                } else {
                    return Response.status(Status.UNAUTHORIZED).build();
                }
            }
        });
        server.addResponse(Method.GET, URI.create("test"), Response.notFound().build());
        server.setPort(7001);
        server.start();
        System.in.read();
        server.stop();
    }

}

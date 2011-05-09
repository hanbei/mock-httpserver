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


import de.hanbei.httpserver.common.Method;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.request.RequestParser;
import de.hanbei.httpserver.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MockHttpServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MockHttpServer.class);

    private int port;
    private ServerSocket serverSocket;

    private Map<Method, URIResponseMapping> predefinedResponses;
    private Response defaultResponse;

    private boolean stopped;
    private boolean isStopping;
    private boolean timeout;

    private final Object lock;

    private long startTimeout = 2000;

    public MockHttpServer() {
        this(80);
    }

    /** Create a new MockHttpServer running on port 80. */
    public MockHttpServer(int port) {
        this.port = port;
        timeout = false;
        lock = new Object();
        predefinedResponses = new HashMap<Method, URIResponseMapping>();
        defaultResponse = Response.notFound().build();
    }

    /** Start the server. */
    public void start() {
        isStopping = false;
        Thread listenerThread = new Thread(this, "MockHttpServer");
        synchronized (lock) {
            listenerThread.start();
            try {
                lock.wait(startTimeout);
            } catch (InterruptedException e) {
                stop();
                LOGGER.error("Error during startup", e);
            }
        }
    }

    /** Stop the server. */
    public void stop() {
        if (!isRunning()) {
            return;
        }
        isStopping = true;
        try {
            if (this.serverSocket == null || this.serverSocket.isClosed()) {
                return;
            }
            InetAddress serverAddress = this.serverSocket.getInetAddress();
            Socket socket = new Socket(serverAddress, this.port);
            socket.close();
            synchronized (lock) {
                lock.wait();
            }

        } catch (ConnectException e) {
            try {
                LOGGER.info("Could not send close request. But server socket is not waiting, just closing it.");
                serverSocket.close();
            } catch (IOException e1) {
                LOGGER.error("Error while stopping server", e);
            }
        } catch (IOException e) {
            LOGGER.warn("Error while stopping server", e);
        } catch (InterruptedException e) {
            LOGGER.warn("Error while stopping server", e);
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
        if (this.serverSocket == null) {
            return !this.stopped;
        }
        return !this.serverSocket.isClosed();
    }

    /** Implementation of the socket listening thread. */
    public void run() {
        try {
            this.serverSocket = new ServerSocket(MockHttpServer.this.port);
            LOGGER.info("Started server on {}:{}",
                    MockHttpServer.this.serverSocket.getInetAddress(),
                    MockHttpServer.this.serverSocket.getLocalPort());
            this.stopped = false;
            synchronized (lock) {
                lock.notifyAll();
            }
            while (!MockHttpServer.this.isStopping) {
                Socket clientSocket = MockHttpServer.this.serverSocket.accept();
                handle(clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (serverSocket != null) {
                    this.serverSocket.close();
                }
            } catch (IOException e) {
                LOGGER.error("Error while closing socket", e);
            }
            this.stopped = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    private void handle(Socket clientSocket) {
        try {
            Request request = readRequest(clientSocket);
            if (request == null || request.isEmpty()) {
                LOGGER.warn("No Request");
                return;
            }

            if (timeout) {
                return;
            }


            if (clientSocket.isClosed()) {
                return;
            }


            Response response = getResponse(request);

            sendResponse(response, clientSocket);
        } catch (IOException e) {
            LOGGER.warn("", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.warn("", e);
            }
        }
    }

    private Response getResponse(Request request) {
        LOGGER.debug(request.toString());
        String requestUri = trimSlashes(request.getRequestUri());
        URIResponseMapping mapping = predefinedResponses.get(request.getMethod());
        Response response = mapping.getResponse(requestUri);
        if (response == null) {
            response = defaultResponse;
        }
        LOGGER.debug(response.toString());
        return response;
    }

    private Request readRequest(Socket clientSocket) throws IOException {
        synchronized (lock) {
            try {
                lock.wait(100);
            } catch (InterruptedException e) {
                return null;
            }
        }
        RequestParser requestParser = new RequestParser();
        clientSocket.getInputStream();
        return requestParser.parse(clientSocket.getInputStream());
    }

    private void sendResponse(Response response, Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(response.toString());
        out.flush();
    }

    /**
     * Get the default response the server should send on any request that is not specified via {@link
     * MockHttpServer#addResponse(de.hanbei.httpserver.common.Method, java.net.URI,
     * de.hanbei.httpserver.response.Response)}.
     *
     * @return The default response.
     * @see MockHttpServer#addResponse(de.hanbei.httpserver.common.Method, java.net.URI,
     *      de.hanbei.httpserver.response.Response)
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
     * Get the maximum time the server is allowed to take for startup.
     *
     * @return The maximum time the server is allowed to take for startup.
     */
    public long getStartTimeout() {
        return startTimeout;
    }

    /**
     * Set the maximum time the server is allowed to take for startup.
     *
     * @param startTimeout The maximum time the server is allowed to take for startup.
     */
    public void setStartTimeout(long startTimeout) {
        this.startTimeout = startTimeout;
    }

    /**
     * Add a specified response for a certain url and method. The uri has to be relative to the server url.
     *
     * @param method   The method the response should be sent on.
     * @param uri      The uri the request will access on. Has to be relative to the server url.
     * @param response The response that should be sent on a request on the specified uri with the specified method.
     */
    public void addResponse(Method method, URI uri, Response response) {
        URIResponseMapping mapping = predefinedResponses.get(method);
        if (mapping == null) {
            mapping = new URIResponseMapping();
            predefinedResponses.put(method, mapping);
        }
        mapping.addResponse(trimSlashes(uri), response);
    }

    private String trimSlashes(URI uri) {
        String uriString = uri.toString();
        return uriString.replaceAll("^/|/$", "");
    }

    public static void main(String[] args) throws IOException {
        MockHttpServer server = new MockHttpServer();
        server.setPort(8079);
        server.start();
        System.in.read();
        server.stop();
    }
}

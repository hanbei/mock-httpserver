package de.hanbei.httpserver;

import de.hanbei.httpserver.common.Method;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.request.RequestParser;
import de.hanbei.httpserver.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
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

    private Object waiter;

    public MockHttpServer() {
        this.port = 80;
        timeout = false;
        waiter = new Object();
        predefinedResponses = new HashMap<Method, URIResponseMapping>();
    }

    public void start() {
        isStopping = false;
        Thread listenerThread = new Thread(this, "MockHttpServer");
        listenerThread.start();
        synchronized (waiter) {
            try {
                waiter.wait();
            } catch (InterruptedException e) {
                LOGGER.error("Error during startup", e);
            }
        }
    }

    public void stop() {
        isStopping = true;
        try {
            if (this.serverSocket == null || this.serverSocket.isClosed()) {
                return;
            }
            InetAddress serverAddress = this.serverSocket.getInetAddress();
            Socket socket = new Socket(serverAddress, this.port);
            socket.close();
            synchronized (waiter) {
                waiter.wait();
            }
        } catch (IOException e) {
            LOGGER.warn("Error while stopping server", e);
        } catch (InterruptedException e) {
            LOGGER.warn("Error while stopping server", e);
        }
    }

    public void setTimeout(boolean shouldTimeout) {
        timeout = shouldTimeout;
    }

    public boolean isTimeoutSet() {
        return timeout;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isRunning() {
        if (this.serverSocket == null) {
            return !this.stopped;
        }
        return !this.serverSocket.isClosed();
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(MockHttpServer.this.port);
            LOGGER.info("Started server on {}:{}",
                    MockHttpServer.this.serverSocket.getInetAddress(),
                    MockHttpServer.this.serverSocket.getLocalPort());
            this.stopped = false;
            synchronized (waiter) {
                waiter.notifyAll();
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
            synchronized (waiter) {
                waiter.notifyAll();
            }
        }
    }

    public void handle(Socket clientSocket) {
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
        URI requestUri = request.getRequestUri();
        URIResponseMapping mapping = predefinedResponses.get(request.getMethod());
        Response response = mapping.getResponse(requestUri);
        if (response == null) {
            response = defaultResponse;
        }
        LOGGER.debug(response.toString());
        return response;
    }

    private Request readRequest(Socket clientSocket) throws IOException {
        synchronized (waiter) {
            try {
                waiter.wait(100);
            } catch (InterruptedException e) {
                return null;
            }
        }
        RequestParser requestParser = new RequestParser();
        clientSocket.getInputStream();
        Request request = null;
        request = requestParser.parse(clientSocket.getInputStream());
        return request;
    }

    private void sendResponse(Response response, Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("HTTP/" + response.getHttpVersion() + " "
                + response.getStatus().toString());
        out.flush();
    }

    public Response getDefaultResponse() {
        return defaultResponse;
    }

    public void setDefaultResponse(Response defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    public void addResponse(Method method, URI uri, Response response) {
        URIResponseMapping mapping = predefinedResponses.get(method);
        if (mapping == null) {
            mapping = new URIResponseMapping();
            predefinedResponses.put(method, mapping);
        }
        mapping.addResponse(uri, response);
    }

    public static void main(String[] args) throws IOException {
        MockHttpServer server = new MockHttpServer();
        server.setPort(8079);
        server.start();
        System.in.read();
        server.stop();
    }
}

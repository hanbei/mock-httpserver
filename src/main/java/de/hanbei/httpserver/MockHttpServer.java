package de.hanbei.httpserver;

import de.hanbei.httpserver.handler.DummyHandler;
import de.hanbei.httpserver.handler.HttpHandler;
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

public class MockHttpServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MockHttpServer.class);

    private int port;
    private ServerSocket serverSocket;
    private HttpHandler handler;

    private boolean stopped;
    private boolean isStopping;
    private boolean timeout;

    private Object waiter;


    public MockHttpServer() {
        this.port = 80;
        handler = new DummyHandler();
        timeout = false;
        waiter = new Object();
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

    public HttpHandler getHandler() {
        return handler;
    }

    public void setHandler(HttpHandler handler) {
        this.handler = handler;
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


            LOGGER.debug(request.toString());
            Response response = handler.handleRequest(request);
            LOGGER.debug(response.toString());

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

    public static void main(String[] args) throws IOException {
        MockHttpServer server = new MockHttpServer();
        server.setPort(8079);
        server.start();
        System.in.read();
        server.stop();
    }
}

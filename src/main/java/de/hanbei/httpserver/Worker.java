/**
 *
 */
package de.hanbei.httpserver;

import de.hanbei.httpserver.handler.HttpHandler;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.request.RequestParser;
import de.hanbei.httpserver.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

class Worker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    private final Socket clientSocket;
    private HttpHandler handler;
    private Object waiter = new Object();
    private boolean timeout;

    public Worker(HttpHandler handler, Socket clientSocket, boolean timeout) {
        this.clientSocket = clientSocket;
        this.handler = handler;
        this.timeout = timeout;
    }

    public void run() {
        try {
            if (timeout) {
                return;
            }

            Request request = readRequest();

            if (clientSocket.isClosed()) {
                return;
            }

            if (request == null || request.isEmpty()) {
                LOGGER.warn("No Request");
                return;
            }

            LOGGER.debug(request.toString());
            Response response = handler.handleRequest(request);
            LOGGER.debug(response.toString());

            sendResponse(response);
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

    private Request readRequest() throws IOException {
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

    private void sendResponse(Response response) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("HTTP/" + response.getHttpVersion() + " "
                + response.getStatus().toString());
        out.flush();
    }
}
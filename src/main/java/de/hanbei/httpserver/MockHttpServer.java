package de.hanbei.httpserver;

import de.hanbei.httpserver.handler.DummyHandler;
import de.hanbei.httpserver.handler.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MockHttpServer implements Runnable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MockHttpServer.class);

	private int port;
	private ServerSocket serverSocket;

	private boolean stopped;
	private HttpHandler handler;
    private boolean timeout;

    public MockHttpServer() {
		this.port = 80;
		handler = new DummyHandler();
        timeout = false;
	}

	public void start() {
		this.stopped = false;
		Thread listenerThread = new Thread(this, "MockHttpServer");
		listenerThread.start();
	}

	public void stop() {
		this.stopped = true;
		try {
			if (this.serverSocket == null || this.serverSocket.isClosed()) {
				return;
			}
			InetAddress serverAddress = this.serverSocket.getInetAddress();
			Socket socket = new Socket(serverAddress, this.port);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
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

			int workerCounter = 0;
			while (!MockHttpServer.this.stopped) {
				Socket clientSocket = MockHttpServer.this.serverSocket.accept();
				Thread t = new Thread(new Worker(handler, clientSocket, timeout),
						"Worker" + (workerCounter++));
				t.start();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			stop();
			try {
				if (serverSocket != null) {
					this.serverSocket.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		MockHttpServer server = new MockHttpServer();
		server.setPort(8079);
		server.start();
		System.in.read();
		server.stop();
	}
}

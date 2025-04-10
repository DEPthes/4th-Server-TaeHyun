package com.hooby.http;

import com.hooby.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;

public class CustomHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(CustomHttpServer.class);

    private final int port;
    private final HttpConnector httpConnector;

    public CustomHttpServer(int port, ServletContainer container) {
        this.port = port;
        this.httpConnector = new HttpConnector(container);
    }

    public void run() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("🟢 HTTP Server is listening on port {}", port);

            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("🟢 Client connected: {}", socket.getInetAddress());
                httpConnector.handle(socket);
            }
        }
    }
}

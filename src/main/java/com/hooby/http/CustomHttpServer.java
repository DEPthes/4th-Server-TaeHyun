package com.hooby.http;

import com.hooby.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService; // for Multi-Threading
import java.util.concurrent.Executors;

public class CustomHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(CustomHttpServer.class);

    private final int port;
    private final HttpConnector httpConnector;

    // Create Thread Pool 🧐 p0 : nThreads 는 어느 정도가 적당할까?
    private final ExecutorService threadPool = Executors.newFixedThreadPool(50);

    // Parameterized Constructor
    public CustomHttpServer(int port, ServletContainer container) {
        this.port = port;
        this.httpConnector = new HttpConnector(container);
    }

    public void run() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("🟢 HTTP Server is listening on port {}", port);

            while (true) {
                Socket connectionSocket = serverSocket.accept();
                logger.info("🟢 Client connected: {}", connectionSocket.getInetAddress());

                // Submit Request Logic to a Thread Pool
                threadPool.submit(() -> httpConnector.handle(connectionSocket));
            }
        } catch (Exception e) {
            logger.error("CustomHttpServer : 예기치 못한 에러 발생", e);
        } finally {
            // 왜 try with resource 로 자동 해제 안함?
            // ExecutorService 는 Closable, AutoClosable Interface 를 구현한게 아니라서
            threadPool.shutdown(); // Clean Memory
        }
    }
}
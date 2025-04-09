package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServletContainer {
    private static final Logger logger = LoggerFactory.getLogger(ServletContainer.class);

    private static final ServletContainer instance = new ServletContainer();
    private final Map<String, Servlet> servletMapping = new HashMap<>();

    private ServletContainer() {}

    public static ServletContainer getInstance() {
        return instance;
    }

    public void registerServlet(String path, Servlet servlet){
        servletMapping.put(path, servlet);
    }

    public void start(int port) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("🟢 HTTP Server is listening on port {}", port);

            while (true) {
                try (Socket connectionSocket = serverSocket.accept()) {
                    logger.info("🟢 Client connected: {}", connectionSocket.getInetAddress());

                    // connectionSocket 으로 부터 사용자의 요청을 가져옴 -> HttpMsg 가 옴
                    CustomHttpRequest request = new CustomHttpRequest(connectionSocket);

                    // response 를 위한 객체를 생성
                    CustomHttpResponse response = new CustomHttpResponse();

                    Servlet servlet = servletMapping.get(request.getPath()); // Servlet 찾기

                    if (servlet != null) {
                        servlet.service(request, response);
                    } else {
                        response.setStatus(HttpStatus.NOT_FOUND);
                        response.setBody("No servlet mapped to " + request.getPath());
                    }

                    String httpResponse = response.toHttpMessage(); // Response 를 HttpMsg 로 만든다.

                    // 클라이언트로 응답 전송
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes(httpResponse);

                } catch (IllegalArgumentException e) {
                    logger.error("🔴 잘못된 요청입니다. : {}", e.getMessage());
                } catch (Exception e) {
                    logger.error("🔴 뭔가 예기치 못한 에러가 발생했어요.", e);
                }
            }
        }
    }
}

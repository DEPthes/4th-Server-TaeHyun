package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import com.hooby.routing.RouteMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServletContainer {
    private static final Logger logger = LoggerFactory.getLogger(ServletContainer.class);

    private static final ServletContainer instance = new ServletContainer();
    private final List<RouteBinding> routes = new ArrayList<>();
    
    private ServletContainer() {}

    public static ServletContainer getInstance() {
        return instance;
    }

    public void registerServlet(String pattern, Servlet servlet) {
        routes.add(new RouteBinding(new RouteMatcher(pattern), servlet));
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

                    boolean matched = false;

                    for (RouteBinding binding : routes) {
                        Map<String, String> pathParams = binding.matcher.match(request.getPath());
                        if (pathParams != null) {
                            request.setPathParams(pathParams);
                            binding.servlet.service(request, response);
                            matched = true;
                            break;
                        }
                    }

                    if (!matched) {
                        response.setStatus(HttpStatus.NOT_FOUND);
                        response.setBody("No servlet mapped to " + request.getPath());
                    }

                    DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
                    out.writeBytes(response.toHttpMessage());

                } catch (IllegalArgumentException e) {
                    logger.error("🔴 잘못된 요청입니다. : {}", e.getMessage());
                } catch (Exception e) {
                    logger.error("🔴 뭔가 예기치 못한 에러가 발생했어요.", e);
                }
            }
        }
    }

    private static class RouteBinding {
        RouteMatcher matcher;
        Servlet servlet;

        RouteBinding(RouteMatcher matcher, Servlet servlet) {
            this.matcher = matcher;
            this.servlet = servlet;
        }
    }
}

package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import com.hooby.parser.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServletContainer {
    private static final Logger logger = LoggerFactory.getLogger(ServletContainer.class);

    private final ServletMapper servletMapper;
    private final ServletInitializer servletInitializer;

    public ServletContainer(ServletMapper servletMapper, ServletInitializer servletInitializer) {
        this.servletMapper = servletMapper;
        this.servletInitializer = servletInitializer;
    }

    public CustomHttpResponse dispatch(CustomHttpRequest request) {
        CustomHttpResponse response = new CustomHttpResponse();

        ServletMapper.MappingResult result = servletMapper.map(request.getPath());

        if (result == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody("Not Found");
        } else {
            request.setPathParams(result.pathParams());
            Servlet servlet = servletInitializer.getOrCreate(result.servletName());
            servlet.service(request, response);
        }

        return response;
    }

    public void start(int port) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("🟢 HTTP Server is listening on port {}", port);

            while (true) {
                try (Socket connectionSocket = serverSocket.accept()) {
                    logger.info("🟢 Client connected: {}", connectionSocket.getInetAddress());

                    // connectionSocket 으로 부터 사용자의 요청을 가져옴 -> HttpMsg 가 옴
                    CustomHttpRequest request = HttpRequestParser.parse(connectionSocket); // Create Parsed HttpRequestObject
                    CustomHttpResponse response = new CustomHttpResponse(); // Create HttpResponseObject

                    ServletMapper.MappingResult result = servletMapper.map(request.getPath());

                    if (result == null) {
                        response.setStatus(HttpStatus.NOT_FOUND);
                        response.setBody("Not Found");
                    } else {
                        request.setPathParams(result.pathParams());
                        Servlet servlet = servletInitializer.getOrCreate(result.servletName());
                        servlet.service(request, response);
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
}

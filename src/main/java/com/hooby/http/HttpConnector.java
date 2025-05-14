package com.hooby.http;

import com.hooby.parser.HttpRequestParser;
import com.hooby.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpConnector {
    private static final Logger logger = LoggerFactory.getLogger(HttpConnector.class);
    private final ServletContainer servletContainer;

    public HttpConnector(ServletContainer servletContainer) {
        this.servletContainer = servletContainer;
    }

    public void handle(Socket connectionSocket) {
        try (connectionSocket;
             OutputStream out = connectionSocket.getOutputStream()
        ) {

            // connectionSocket 으로 부터 사용자의 요청을 가져옴 -> HttpMsg 가 옴
            CustomHttpRequest request = HttpRequestParser.parse(connectionSocket); // Create Parsed HttpRequestObject

//            CustomHttpResponse response = new CustomHttpResponse();
            CustomHttpResponse response;

            response = servletContainer.dispatch(request); // Create HttpResponseObject

            byte[] httpBytes = response.toHttpMessage().getBytes(StandardCharsets.UTF_8);
            out.write(httpBytes); // try-with 라 자동으로 닫혀줄거임
            System.out.println("응답:\n" + response.toHttpMessage());

        } catch (IllegalArgumentException e) {
            logger.error("🔴 잘못된 요청입니다. : {}", e.getMessage());
        } catch (Exception e) {
            logger.error("🔴 뭔가 예기치 못한 에러가 발생했어요.", e);
        }
    }
}

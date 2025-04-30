package com.hooby;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.CustomHttpServer;
import com.hooby.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            ServletMapper mapper = new ServletMapper();
            mapper.registerServlet("/users", "UserServlet");
            mapper.registerServlet("/users/{id}", "UserServlet");
            mapper.registerServlet("/test", "TestServlet");

            ServletInitializer initializer = new ServletInitializer();
            initializer.registerFactory("UserServlet", UserServlet::new);
            initializer.registerFactory("TestServlet", () -> new Servlet() {
                @Override
                public void service(CustomHttpRequest request, CustomHttpResponse response) {
                    try {
                        Thread.sleep(500);
                        response.setBody("OK");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            ServletContainer container = new ServletContainer(mapper, initializer);
            CustomHttpServer server = new CustomHttpServer(8080, container);
            server.run();

        } catch (Exception e) {
            logger.error("🔴 뭔가 예기치 못한 에러가 발생했습니다.", e);
        }
    }
}
package com.hooby;

import com.hooby.http.*;
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
            mapper.registerServlet("/login", "UserServlet");

            ServletInitializer initializer = new ServletInitializer();

            UserServlet sharedUserServlet = new UserServlet();
            initializer.registerFactory("UserServlet", () -> sharedUserServlet);

            initializer.registerFactory("TestServlet", () -> new Servlet() {
                @Override
                public void service(CustomHttpRequest request, CustomHttpResponse response) {
                    try {
                        String sessionUser = (String) request.getSession().getAttribute("user");
                        Thread.sleep(500);
                        response.setBody("Hello " + (sessionUser != null ? sessionUser : "guest"));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // 3. 필터 설정 (순서 중요)
            FilterManager filterManager = new FilterManager();
            filterManager.addFilter(new SessionFilter());   // 반드시 제일 먼저
            filterManager.addFilter(new LoggingFilter());
            filterManager.addFilter(new AuthFilter());

            ServletContainer container = new ServletContainer(mapper, initializer, filterManager);
            CustomHttpServer server = new CustomHttpServer(8080, container);

            server.run();

        } catch (Exception e) {
            logger.error("🔴 뭔가 예기치 못한 에러가 발생했습니다.", e);
        }
    }
}
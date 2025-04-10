package com.hooby;

import com.hooby.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // 1. 서블릿 매퍼 생성 및 경로 등록
            ServletMapper mapper = new ServletMapper();
            mapper.registerServlet("/hello", "TestServlet");
            mapper.registerServlet("/user/{id}", "TestUserServlet");
            mapper.registerServlet("/users", "UserServlet");
            mapper.registerServlet("/users/{id}", "UserServlet");

            // 2. 서블릿 이니셜라이저 생성 및 팩토리 등록
            ServletInitializer initializer = new ServletInitializer();
            initializer.registerFactory("TestServlet", TestServlet::new);
            initializer.registerFactory("TestUserServlet", TestUserServlet::new);
            initializer.registerFactory("UserServlet", UserServlet::new);

            // 3. 서블릿 컨테이너 생성 및 시작
            ServletContainer container = new ServletContainer(mapper, initializer);
            container.start(8080); // 브라우저: http://localhost:8080/hello 등 테스트 가능

        } catch (Exception e) {
            logger.error("🔴 뭔가 예기치 못한 에러가 발생했습니다.", e);
        }
    }
}
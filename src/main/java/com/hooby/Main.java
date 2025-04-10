package com.hooby;

import com.hooby.servlet.TestServlet;
import com.hooby.servlet.ServletContainer;
import com.hooby.servlet.TestUserServlet;
import com.hooby.servlet.UserServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            ServletContainer container = ServletContainer.getInstance();

            // /hello 경로에 대한 Servlet 등록
            container.registerServlet("/hello", new TestServlet());
            container.registerServlet("/user/{id}", new TestUserServlet());

            UserServlet userServlet = new UserServlet();
            container.registerServlet("/users/{id}", userServlet);
            container.registerServlet("/users", userServlet);

            // 서버 실행
            container.start(8080); // 브라우저에서 http://localhost:8080/hello 로 접속 가능

        } catch (Exception e) {
            logger.error("🔴 뭔가 예기치 못한 에러가 발생했습니다.", e);
        }
    }
}
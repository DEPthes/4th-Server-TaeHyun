package com.hooby;

import com.hooby.http.*;
import com.hooby.ioc.ApplicationContext;
import com.hooby.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            ApplicationContext context = new ApplicationContext("beans.xml");
            ServletContainer container = (ServletContainer) context.getBean("servletContainer");
            CustomHttpServer server = new CustomHttpServer(8080, container);
            server.run();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("🟡 Shutdown Hook 실행 중...");
                context.close();
            }));
        } catch (Exception e) {
            logger.error("🔴 예기치 못한 에러", e);
        }
    }
}
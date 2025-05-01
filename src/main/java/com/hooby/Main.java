package com.hooby;

import com.hooby.http.*;
import com.hooby.listener.ListenerManager;
import com.hooby.listener.SessionListener;
import com.hooby.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Servlet Mapping
            ServletMapper mapper = new ServletMapper();
            mapper.registerServlet("/users", "UserServlet");
            mapper.registerServlet("/users/{id}", "UserServlet");
            mapper.registerServlet("/test", "TestServlet");
            mapper.registerServlet("/login", "UserServlet");

            // Servlet Init
            ServletInitializer initializer = new ServletInitializer();

            UserServlet sharedUserServlet = new UserServlet();
            initializer.registerFactory("UserServlet", () -> sharedUserServlet);
            initializer.registerFactory("TestServlet", () -> (req, res) -> {
                try {
                    String sessionUser = (String) req.getSession().getAttribute("user");
                    Thread.sleep(500);
                    res.setBody("Hello " + (sessionUser != null ? sessionUser : "guest"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            // Listener
            setupListenerManager();

            // FilterManager
            FilterManager filterManager = new FilterManager();
            filterManager.addFilter(new SessionFilter());   // 반드시 제일 먼저
            filterManager.addFilter(new LoggingFilter());
            filterManager.addFilter(new AuthFilter());

            // Servlet Container
            ServletContainer container = new ServletContainer(mapper, initializer, filterManager);
            CustomHttpServer server = new CustomHttpServer(8080, container);

            // Run
            server.run();

        } catch (Exception e) {
            logger.error("🔴 뭔가 예기치 못한 에러가 발생했습니다.", e);
        }
    }

    private static void setupListenerManager() {
        ListenerManager listenerManager = new ListenerManager();
        listenerManager.addSessionListener(new SessionListener() {
            @Override
            public void onSessionCreated(Session session) {
                System.out.println("🟢 Listener: 세션 생성됨 → " + session.getId());
            }

            @Override
            public void onSessionDestroyed(Session session) {
                System.out.println("🔴 Listener: 세션 제거됨 → " + session.getId());
            }
        });
        SessionManager.setListenerManager(listenerManager);
    }
}
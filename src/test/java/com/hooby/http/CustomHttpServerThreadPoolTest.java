package com.hooby.http;

import com.hooby.servlet.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomHttpServerThreadPoolTest {

    @Test
    public void testConcurrentRequestHandling() throws Exception {
        int port = 9090;
        int requestCount = 5;
        CountDownLatch latch = new CountDownLatch(requestCount);

        // 1. 서블릿 매핑 및 등록
        ServletMapper mapper = new ServletMapper();
        mapper.registerServlet("/test", "TestServlet");

        ServletInitializer initializer = new ServletInitializer();
        initializer.registerFactory("TestServlet", () -> new Servlet() {
            @Override
            public void service(CustomHttpRequest request, CustomHttpResponse response) {
                try {
                    String threadName = Thread.currentThread().getName();
                    String user = (String) request.getSession().getAttribute("user");
                    System.out.println("요청 처리 스레드: " + threadName + ", user: " + user);
                    Thread.sleep(500);
                    response.setBody("Authorized : " + user);
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 2. 필터 구성
        FilterManager filterManager = new FilterManager();
        filterManager.addFilter(new LoggingFilter());
        filterManager.addFilter(new AuthFilter());

        ServletContainer container = new ServletContainer(mapper, initializer, filterManager);

        // 3. 미리 로그인 된 세션 등록
        for (int i = 0; i < 3; i++) {
            String sessionId = "test-session-" + i;
            Session session = new Session(sessionId);
            session.setAttribute("user", "hooby-" + i);
            SessionManager.injectSession(sessionId, session);
        }

        // 4. 서버 스레드 구동
        CustomHttpServer server = new CustomHttpServer(port, container);
        new Thread(() -> {
            try {
                server.run();
            } catch (Exception ignored) {}
        }).start();

        Thread.sleep(1000); // 서버 기동 대기

        // 5. 클라이언트 요청 스레드 생성 -> 5개는 로그인이고 5개는 비로그인임. 3번 보면 된다.
        for (int i = 0; i < requestCount; i++) {
            final int index = i;
            new Thread(() -> {
                try (Socket socket = new Socket("localhost", port);
                     OutputStream os = socket.getOutputStream();
                     InputStream is = socket.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                    String sessionHeader = index < 3
                            ? "Cookie: JSESSIONID=test-session-" + index + "\r\n"
                            : ""; // 비로그인 요청

                    String rawRequest = "GET /test HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            sessionHeader +
                            "\r\n";

                    os.write(rawRequest.getBytes());
                    os.flush();

                    // 응답 헤더 읽기 (Broken pipe 방지용)
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("Authorized : ")) {
                            System.out.println("[request]: " + line);
                        }
                        if (line.isEmpty()) break;
                    }

                    String body = reader.readLine();
                    System.out.println("[body] " + body);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        // 6. 처리 시간 측정 및 확인
        long start = System.currentTimeMillis();
        boolean completed = latch.await(2, java.util.concurrent.TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("총 처리 시간(ms): " + elapsed);
    }
}

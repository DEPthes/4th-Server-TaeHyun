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
        int requestCount = 10;
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
                    System.out.println("요청 처리 스레드: " + threadName); // 🔍 스레드 이름 출력

                    Thread.sleep(500); // simulate long task
                    response.setBody("OK");

                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ServletContainer container = new ServletContainer(mapper, initializer);

        // 2. 서버 스레드 구동
        CustomHttpServer server = new CustomHttpServer(port, container);
        new Thread(() -> {
            try {
                server.run();
            } catch (Exception ignored) {}
        }).start();

        Thread.sleep(1000); // 서버 기동 대기

        // 3. 클라이언트 요청 스레드 생성
        for (int i = 0; i < requestCount; i++) {
            new Thread(() -> {
                try (Socket socket = new Socket("localhost", port);
                     OutputStream os = socket.getOutputStream();
                     InputStream is = socket.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                    os.write("GET /test HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes());
                    os.flush();

                    // 응답 헤더 읽기 (Broken pipe 방지용)
                    String line;
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        // 응답 생략
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        // 4. 처리 시간 측정 및 확인
        long start = System.currentTimeMillis();
        boolean completed = latch.await(2, java.util.concurrent.TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("총 처리 시간(ms): " + elapsed);
        assertTrue(completed && elapsed < 1500, "병렬 처리되어야 함 (1.5초 내 완료)");
    }
}

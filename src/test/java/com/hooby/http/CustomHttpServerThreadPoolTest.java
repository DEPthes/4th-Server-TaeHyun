package com.hooby.http;

import com.hooby.servlet.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomHttpServerThreadPoolTest {

    @Test
    public void testThreadPoolConcurrency() throws Exception {
        int port = 9090;
        int requestCount = 5;
        CountDownLatch latch = new CountDownLatch(requestCount);

        // 1. 서블릿 매핑 및 단일 테스트 서블릿 등록
        ServletMapper mapper = new ServletMapper();
        mapper.registerServlet("/test", "TestServlet");

        ServletInitializer initializer = new ServletInitializer();
        initializer.registerFactory("TestServlet", () -> (req, res) -> {
            try {
                String thread = Thread.currentThread().getName();
                System.out.println("💡 요청 처리 스레드: " + thread);
                Thread.sleep(500);  // 병렬성 확인용
                res.setBody("Hello from " + thread);
                latch.countDown();
            } catch (InterruptedException ignored) {}
        });

        // 2. 필터는 없이 구성 (ThreadPool 테스트 목적)
        FilterManager filterManager = new FilterManager();

        ServletContainer container = new ServletContainer(mapper, initializer, filterManager);

        // 3. 서버 실행
        CustomHttpServer server = new CustomHttpServer(port, container);
        new Thread(() -> {
            try {
                server.run();
            } catch (Exception ignored) {}
        }).start();

        Thread.sleep(500); // 서버 기동 대기

        // 4. 요청 5개 병렬 발사
        for (int i = 0; i < requestCount; i++) {
            new Thread(() -> {
                try (Socket socket = new Socket("localhost", port);
                     OutputStream os = socket.getOutputStream();
                     InputStream is = socket.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                    String rawRequest = "GET /test HTTP/1.1\r\n" +
                            "Host: localhost\r\n\r\n";

                    os.write(rawRequest.getBytes());
                    os.flush();

                    while (reader.readLine() != null) {
                        // 응답 헤더 버리기
                        if (reader.readLine().isEmpty()) break;
                    }

                    System.out.println("[응답 본문] " + reader.readLine());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        // 5. 완료 대기
        boolean completed = latch.await(3, java.util.concurrent.TimeUnit.SECONDS);
        assertTrue(completed, "요청 5개가 모두 병렬로 처리되어야 함");
    }
}
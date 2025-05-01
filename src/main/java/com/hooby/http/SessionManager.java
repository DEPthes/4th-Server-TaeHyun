package com.hooby.http;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> sessionStore = new ConcurrentHashMap<>();
    private static final String COOKIE_NAME = "JSESSIONID";

    public static void injectSession(String sessionId, Session session) {
        sessionStore.put(sessionId, session);
    }

    public static Session getOrCreateSession(CustomHttpRequest request, CustomHttpResponse response) {
        String sessionId = null;

        // 1. 요청 헤더에서 JSESSIONID 쿠키 추출
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                String[] pair = cookie.trim().split("=", 2);
                if (pair.length == 2 && COOKIE_NAME.equals(pair[0])) {
                    sessionId = pair[1];
                    break;
                }
            }
        }

        // 2. 세션 조회 or 생성
        Session session;
        if (sessionId != null && sessionStore.containsKey(sessionId)) {
            session = sessionStore.get(sessionId);
        } else {
            sessionId = UUID.randomUUID().toString();
            session = new Session(sessionId);
            sessionStore.put(sessionId, session);
            response.setHeader("Set-Cookie", COOKIE_NAME + "=" + sessionId + "; Path=/; HttpOnly");
            System.out.println("Session created: " + sessionId + " | Thread: " + Thread.currentThread().getName());
        }

        // ✅ 핵심: 세션을 request 에 세팅
        request.setSession(session);

        return session;
    }
}
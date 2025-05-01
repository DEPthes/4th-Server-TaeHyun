package com.hooby.http;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessionStore = new ConcurrentHashMap<>();
    private static final String COOKIE_NAME = "JSESSIONID";

    public static Session getOrCreateSession(CustomHttpRequest request, CustomHttpResponse response) {
        String cookieHeader = request.getHeader("Cookie");
        String extractedId = extractSessionIdFromCookie(cookieHeader);

        if (extractedId != null && sessionStore.containsKey(extractedId)) {
            Session session = sessionStore.get(extractedId);
            request.setSession(session);
            return session;
        }

        // 새 세션 생성
        String newId = UUID.randomUUID().toString();
        Session session = new Session(newId);
        sessionStore.put(newId, session);

        System.out.println("Session created: " + newId + " | Thread: " + Thread.currentThread().getName());

        response.setHeader("Set-Cookie", COOKIE_NAME + "=" + newId + "; Path=/; HttpOnly");
        request.setSession(session);
        return session;
    }

    private static String extractSessionIdFromCookie(String cookieHeader) {
        if (cookieHeader == null) return null;

        for (String cookie : cookieHeader.split(";")) {
            cookie = cookie.trim();
            if (cookie.startsWith(COOKIE_NAME + "=")) {
                return cookie.substring((COOKIE_NAME + "=").length());
            }
        }
        return null;
    }

    // JUnit 외 사용 금지. 그냥 테스트 전용이니까
    static void injectSession(String sessionId, Session session) {
        sessionStore.put(sessionId, session);
    }
}
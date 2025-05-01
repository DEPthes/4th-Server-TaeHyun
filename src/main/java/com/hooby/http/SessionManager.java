package com.hooby.http;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static Session getOrCreateSession(CustomHttpRequest request, CustomHttpResponse response) {
        String sessionId = request.getHeader("Cookie");
        String extractedId = null;

        if (sessionId != null && sessionId.startsWith("JSESSIONID=")) {
            extractedId = sessionId.substring("JSESSIONID=".length());
        }

        if (extractedId != null && sessions.containsKey(extractedId)) {
            return sessions.get(extractedId);
        }

        // 새 세션 발급
        String newId = UUID.randomUUID().toString();
        Session session = new Session(newId);
        sessions.put(newId, session);

        System.out.println("Session created: " + newId + " | Thread: " + Thread.currentThread().getName());

        response.setHeader("Set-Cookie", "JSESSIONID=" + newId + "; Path=/; HttpOnly");
        return session;
    }
}
package com.hooby.http;

import com.hooby.listener.ListenerManager;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessionStore = new ConcurrentHashMap<>();
    private static final String COOKIE_NAME = "JSESSIONID";
    private static ListenerManager listenerManager;

    public static void setListenerManager(ListenerManager manager) {
        listenerManager = manager;
    }

    public static Session getOrCreateSession(CustomHttpRequest request, CustomHttpResponse response) {
        // 이미 세션이 존재한다면 바로 반환 (중복 방지)
        if (request.getSession() != null) {
            return request.getSession();
        }

        cleanUpExpiredSessions();

        String cookieHeader = request.getHeader("Cookie");
        String extractedId = extractSessionIdFromCookie(cookieHeader);

        if (extractedId != null && sessionStore.containsKey(extractedId)) {
            Session session = sessionStore.get(extractedId);
            if (!session.isExpired()) {
                session.updateLastAccessedTime();
                request.setSession(session);
                return session;
            } else {
                invalidateSession(extractedId);
            }
        }

        // 새 세션 생성
        String newId = UUID.randomUUID().toString();
        Session session = new Session(newId);
        sessionStore.put(newId, session);
        request.setSession(session);

        System.out.println("Session created: " + newId + " | Thread: " + Thread.currentThread().getName());

        response.setHeader("Set-Cookie", COOKIE_NAME + "=" + newId + "; Path=/; HttpOnly");

        if (listenerManager != null) {
            listenerManager.notifySessionCreated(session);
        }

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

    public static void invalidateSession(String sessionId) {
        Session session = sessionStore.remove(sessionId);
        if (session != null && listenerManager != null) {
            listenerManager.notifySessionDestroyed(session);
        }
    }

    private static void cleanUpExpiredSessions() {
        Iterator<Map.Entry<String, Session>> it = sessionStore.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Session> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                if (listenerManager != null) {
                    listenerManager.notifySessionDestroyed(entry.getValue());
                }
            }
        }
    }

    // JUnit 외 사용 금지. 그냥 테스트 전용이니까
    static void injectSession(String sessionId, Session session) {
        sessionStore.put(sessionId, session);
    }
}
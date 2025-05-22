package com.hooby.http;

import com.hooby.filter.SessionFilter;
import com.hooby.listener.ListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private final Map<String, Session> sessionStore = new ConcurrentHashMap<>();
    private final String COOKIE_NAME = "JSESSIONID";

    private final ListenerManager listenerManager;

    public SessionManager(ListenerManager listenerManager) {
        this.listenerManager = listenerManager;
        logger.info("🧩 생성자 주입됨: {}", listenerManager.getClass().getSimpleName());
    }

    public Session getOrCreateSession(CustomHttpRequest request, CustomHttpResponse response) {
        if (request.getSession() != null) return request.getSession();
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

        String newId = UUID.randomUUID().toString();
        Session session = new Session(newId);
        sessionStore.put(newId, session);
        request.setSession(session);

        logger.info("Session created: " + newId + " | Thread: " + Thread.currentThread().getName());
        response.setHeader("Set-Cookie", COOKIE_NAME + "=" + newId + "; Path=/; HttpOnly");

        listenerManager.notifySessionCreated(session);
        return session;
    }

    private String extractSessionIdFromCookie(String cookieHeader) {
        if (cookieHeader == null) return null;
        for (String cookie : cookieHeader.split(";")) {
            cookie = cookie.trim();
            if (cookie.startsWith(COOKIE_NAME + "=")) {
                return cookie.substring((COOKIE_NAME + "=").length());
            }
        }
        return null;
    }

    public void invalidateSession(String sessionId) {
        Session session = sessionStore.remove(sessionId);
        if (session != null) {
            listenerManager.notifySessionDestroyed(session);
        }
    }

    private void cleanUpExpiredSessions() {
        Iterator<Map.Entry<String, Session>> it = sessionStore.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Session> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                listenerManager.notifySessionDestroyed(entry.getValue());
            }
        }
    }

    public void clearAll() {
        logger.info("🔴 SessionManager 모든 세션 초기화");
        for (Session s : sessionStore.values()) {
            listenerManager.notifySessionDestroyed(s);
        }
        sessionStore.clear();
    }
}
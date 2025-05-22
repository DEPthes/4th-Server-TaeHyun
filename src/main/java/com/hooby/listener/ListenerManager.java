package com.hooby.listener;

import com.hooby.http.Session;
import com.hooby.servlet.DispatcherServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    private static final Logger logger = LoggerFactory.getLogger(ListenerManager.class);
    private final List<ServerListener> serverListeners;
    private final List<SessionListener> sessionListeners;

    public ListenerManager(List<ServerListener> serverListeners, List<SessionListener> sessionListeners) {
        this.serverListeners = serverListeners;
        this.sessionListeners = sessionListeners;

        logger.info("🧩 생성자 주입됨: ServerListeners={}, SessionListeners={}", serverListeners.size(), sessionListeners.size());

        sessionListeners.forEach(l -> logger.info("   - SessionListener: {}", l.getClass().getSimpleName()));
    }

    public void notifyInit() {
        logger.info("🟢 ListenerManager 초기화됨");
        serverListeners.forEach(ServerListener::onInit);
    }

    public void notifyDestroy() {
        logger.info("🔴 ListenerManager 종료됨");
        serverListeners.forEach(ServerListener::onDestroy);
    }

    public void notifySessionCreated(Session session) {
        sessionListeners.forEach(listener -> listener.onSessionCreated(session));
    }

    public void notifySessionDestroyed(Session session) {
        sessionListeners.forEach(listener -> listener.onSessionDestroyed(session));
    }

}
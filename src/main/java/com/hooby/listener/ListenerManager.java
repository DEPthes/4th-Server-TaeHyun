package com.hooby.listener;

import com.hooby.http.Session;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    private final List<ServerListener> serverListeners;
    private final List<SessionListener> sessionListeners;

    public ListenerManager(List<ServerListener> serverListeners, List<SessionListener> sessionListeners) {
        this.serverListeners = serverListeners;
        this.sessionListeners = sessionListeners;

        System.out.println("🧩 생성자 주입됨: ServerListeners=" + serverListeners.size()
                + ", SessionListeners=" + sessionListeners.size());

        sessionListeners.forEach(l -> System.out.println("   - SessionListener: " + l.getClass().getSimpleName()));
    }

    public void notifyInit() {
        System.out.println("🟢 ListenerManager 초기화됨");
        serverListeners.forEach(ServerListener::onInit);
    }

    public void notifyDestroy() {
        System.out.println("🔴 ListenerManager 종료됨");
        serverListeners.forEach(ServerListener::onDestroy);
    }

    public void notifySessionCreated(Session session) {
        sessionListeners.forEach(listener -> listener.onSessionCreated(session));
    }

    public void notifySessionDestroyed(Session session) {
        sessionListeners.forEach(listener -> listener.onSessionDestroyed(session));
    }

}
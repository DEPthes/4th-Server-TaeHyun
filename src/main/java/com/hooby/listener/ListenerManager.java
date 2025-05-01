package com.hooby.listener;

import com.hooby.http.Session;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    private final List<ServerListener> serverListeners = new ArrayList<>();
    private final List<SessionListener> sessionListeners = new ArrayList<>();

    public void addServerListener(ServerListener listener) {
        serverListeners.add(listener);
    }

    public void addSessionListener(SessionListener listener) {
        sessionListeners.add(listener);
    }

    public void notifyInit() {
        serverListeners.forEach(ServerListener::onInit);
    }

    public void notifyDestroy() {
        serverListeners.forEach(ServerListener::onDestroy);
    }

    public void notifySessionCreated(Session session) {
        sessionListeners.forEach(listener -> listener.onSessionCreated(session));
    }

    public void notifySessionDestroyed(Session session) {
        sessionListeners.forEach(listener -> listener.onSessionDestroyed(session));
    }
}
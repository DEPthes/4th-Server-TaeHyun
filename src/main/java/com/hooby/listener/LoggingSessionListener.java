package com.hooby.listener;

import com.hooby.http.Session;

public class LoggingSessionListener implements SessionListener {
    @Override
    public void onSessionCreated(Session session) {
        System.out.println("🟢 Listener: 세션 생성됨 → " + session.getId());
    }

    @Override
    public void onSessionDestroyed(Session session) {
        System.out.println("🔴 Listener: 세션 제거됨 → " + session.getId());
    }
}
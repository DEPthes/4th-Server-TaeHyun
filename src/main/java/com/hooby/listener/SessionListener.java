package com.hooby.listener;

import com.hooby.http.Session;

public interface SessionListener {
    default void onSessionCreated(Session session) {}
    default void onSessionDestroyed(Session session) {}
}
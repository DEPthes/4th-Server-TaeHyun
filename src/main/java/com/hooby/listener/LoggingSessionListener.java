package com.hooby.listener;

import com.hooby.http.Session;
import com.hooby.servlet.OrderServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSessionListener implements SessionListener {
    private static final Logger logger = LoggerFactory.getLogger(LoggingSessionListener.class);
    @Override
    public void onSessionCreated(Session session) { logger.info("🟢 Listener: 세션 생성됨 → {}", session.getId());}

    @Override
    public void onSessionDestroyed(Session session) {
        logger.info("🔴 Listener: 세션 제거됨 → {}", session.getId());
    }
}
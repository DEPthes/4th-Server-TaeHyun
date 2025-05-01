package com.hooby.listener;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.Session;
import com.hooby.http.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SessionListenerTest {

    private List<String> eventLog;

    @BeforeEach
    public void setUp() {
        eventLog = new ArrayList<>();

        ListenerManager manager = new ListenerManager();
        manager.addSessionListener(new SessionListener() {
            @Override
            public void onSessionCreated(Session session) {
                String msg = "🟢 Listener: 세션 생성됨 → " + session.getId();
                eventLog.add("created:" + session.getId());
                System.out.println(msg);
            }

            @Override
            public void onSessionDestroyed(Session session) {
                String msg = "🔴 Listener: 세션 제거됨 → " + session.getId();
                eventLog.add("destroyed:" + session.getId());
                System.out.println(msg);
            }
        });

        SessionManager.setListenerManager(manager);
    }

    @Test
    public void testSessionCreateAndDestroyTriggersListener() {
        CustomHttpRequest request = new CustomHttpRequest();
        CustomHttpResponse response = new CustomHttpResponse();

        Session session = SessionManager.getOrCreateSession(request, response);
        String sessionId = session.getId();

        SessionManager.invalidateSession(sessionId);

        assertEquals(2, eventLog.size());
        assertEquals("created:" + sessionId, eventLog.get(0));
        assertEquals("destroyed:" + sessionId, eventLog.get(1));
    }

    @Test
    public void testOnlyCreateWhenDestroyNotCalled() {
        CustomHttpRequest request = new CustomHttpRequest();
        CustomHttpResponse response = new CustomHttpResponse();

        Session session = SessionManager.getOrCreateSession(request, response);
        String sessionId = session.getId();

        assertEquals(1, eventLog.size());
        assertEquals("created:" + sessionId, eventLog.get(0));
    }

    @Test
    public void testNoListenerSetDoesNotFail() {
        SessionManager.setListenerManager(null);

        CustomHttpRequest request = new CustomHttpRequest();
        CustomHttpResponse response = new CustomHttpResponse();

        Session session = SessionManager.getOrCreateSession(request, response);
        SessionManager.invalidateSession(session.getId());

        assertDoesNotThrow(() -> {});
    }
}
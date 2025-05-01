package com.hooby.http;

import com.hooby.param.QueryParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

    private CustomHttpRequest request;
    private CustomHttpResponse response;

    @BeforeEach
    public void setUp() {
        request = new CustomHttpRequest();
        response = new CustomHttpResponse();

        request.setMethod("GET");
        request.setPath("/test");
        request.setHttpVersion("HTTP/1.1");
        request.setQueryParams(new QueryParams(null));
    }

    @Test
    public void testSessionIsCreatedAndSetInResponse() {
        Session session = SessionManager.getOrCreateSession(request, response);

        assertNotNull(session, "세션이 생성되어야 함");
        assertNotNull(session.getId(), "세션 ID는 null이면 안됨");

        String responseText = response.toHttpMessage();
        assertTrue(responseText.contains("Set-Cookie: JSESSIONID="), "응답에 JSESSIONID가 포함되어야 함");

        System.out.println("세션 생성됨: " + session.getId());
    }

    @Test
    public void testSessionIsReusedIfCookiePresent() {
        Session firstSession = SessionManager.getOrCreateSession(request, response);
        String sessionId = firstSession.getId();

        CustomHttpRequest secondRequest = new CustomHttpRequest();
        secondRequest.setHeader("Cookie", "JSESSIONID=" + sessionId);

        CustomHttpResponse dummyResp = new CustomHttpResponse();
        Session reusedSession = SessionManager.getOrCreateSession(secondRequest, dummyResp);

        assertEquals(sessionId, reusedSession.getId(), "같은 세션 ID로 재사용되어야 함");
        assertSame(firstSession, reusedSession, "세션 객체도 동일해야 함");

        System.out.println("세션 재사용됨: " + sessionId);
    }

    @Test
    public void testSessionAttributeStorage() {
        Session session = SessionManager.getOrCreateSession(request, response);

        session.setAttribute("user", "hooby");
        Object value = session.getAttribute("user");

        assertEquals("hooby", value, "세션에 저장한 값을 다시 정확히 읽을 수 있어야 함");

        System.out.println("세션 속성 확인: user=" + value);
    }

    @Test
    public void testSessionExpiresAfterInactivity() throws InterruptedException {
        Session session = SessionManager.getOrCreateSession(request, response);
        String sessionId = session.getId();

        session.setMaxInactiveInterval(1); // 1초 후 만료
        Thread.sleep(1500); // 만료될 시간 이후로 대기

        CustomHttpRequest nextReq = new CustomHttpRequest();
        nextReq.setHeader("Cookie", "JSESSIONID=" + sessionId);
        CustomHttpResponse dummyResp = new CustomHttpResponse();

        Session newSession = SessionManager.getOrCreateSession(nextReq, dummyResp);

        assertNotEquals(sessionId, newSession.getId(), "만료된 세션과는 다른 새 세션이어야 함");

        System.out.println("만료된 세션 제거됨. 새로운 세션 생성됨: " + newSession.getId());
    }
}
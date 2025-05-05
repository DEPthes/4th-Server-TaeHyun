package com.hooby.servlet;

import com.hooby.filter.AuthFilter;
import com.hooby.filter.Filter;
import com.hooby.filter.FilterChain;
import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import com.hooby.http.Session;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuthFilterChainTest {

    @Test
    public void testUnauthorizedWhenNoUserInSession() {
        StringBuilder log = new StringBuilder();

        Filter sessionFilter = (req, res, chain) -> {
            System.out.println("➡️ SessionFilter 진입");
            log.append("S-");
            if (req.getSession() == null) {
                req.setSession(new Session("unauth-session"));
            }
            chain.doFilter(req, res);
            log.append("-S");
            System.out.println("⬅️ SessionFilter 종료");
        };

        Filter loggingFilter = (req, res, chain) -> {
            System.out.println("➡️ LoggingFilter 진입");
            log.append("L-");
            chain.doFilter(req, res);
            log.append("-L");
            System.out.println("⬅️ LoggingFilter 종료");
        };

        AuthFilter authFilter = new AuthFilter() {
            @Override
            public void doFilter(CustomHttpRequest req, CustomHttpResponse res, FilterChain chain) {
                System.out.println("➡️ AuthFilter 진입");
                log.append("A-");
                super.doFilter(req, res, chain);
                log.append("-A");
                System.out.println("⬅️ AuthFilter 종료");
            }
        };

        CustomHttpRequest request = new CustomHttpRequest(); // path intentionally left null
        request.setPath("/test");
        CustomHttpResponse response = new CustomHttpResponse();

        FilterChain chain = new FilterChain(
                List.of(sessionFilter, loggingFilter, authFilter),
                (req, res) -> fail("🔴 Servlet should not be called")
        );

        chain.doFilter(request, response);

        System.out.println("최종 log: " + log);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertTrue(response.getBody().contains("Unauthorized"));
    }

    @Test
    public void testAuthorizedWhenUserInSessionAndFilterOrderCorrect() {
        StringBuilder log = new StringBuilder();

        Filter sessionFilter = (req, res, chain) -> {
            System.out.println("\n➡️ SessionFilter 진입");
            log.append("S-");
            if (req.getSession() == null) {
                Session session = new Session("auth-session");
                session.setAttribute("user", "hooby");
                req.setSession(session);
            }
            chain.doFilter(req, res);
            log.append("-S");
            System.out.println("⬅️ SessionFilter 종료");
        };

        Filter loggingFilter = (req, res, chain) -> {
            System.out.println("➡️ LoggingFilter 진입");
            log.append("L-");
            chain.doFilter(req, res);
            log.append("-L");
            System.out.println("⬅️ LoggingFilter 종료");
        };

        AuthFilter authFilter = new AuthFilter() {
            @Override
            public void doFilter(CustomHttpRequest req, CustomHttpResponse res, FilterChain chain) {
                System.out.println("➡️ AuthFilter 진입");
                log.append("A-");
                super.doFilter(req, res, chain);
                log.append("-A");
                System.out.println("⬅️ AuthFilter 종료");
            }
        };

        CustomHttpRequest request = new CustomHttpRequest();
        request.setPath("/test");
        CustomHttpResponse response = new CustomHttpResponse();

        Servlet servlet = (req, res) -> {
            System.out.println("🟢 Servlet 실행");
            log.append("V");
            res.setStatus(HttpStatus.OK);
            res.setBody("Authorized access");
        };

        FilterChain chain = new FilterChain(
                List.of(sessionFilter, loggingFilter, authFilter),
                servlet
        );

        chain.doFilter(request, response);

        System.out.println("최종 log: " + log);
        assertEquals("S-L-A-V-A-L-S", log.toString(), "필터 실행 순서 확인");
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Authorized access", response.getBody());
    }
}
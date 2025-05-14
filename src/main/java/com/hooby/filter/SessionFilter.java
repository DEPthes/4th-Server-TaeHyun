package com.hooby.filter;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.SessionManager;

public class SessionFilter implements Filter {
    private final SessionManager sessionManager;

    public SessionFilter(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        System.out.println("🧩 생성자 주입됨: SessionManager into SessionFilter");
    }

    @Override
    public void doFilter(CustomHttpRequest request, CustomHttpResponse response, FilterChain chain) {
        sessionManager.getOrCreateSession(request, response); // 세션을 항상 생성 또는 재활용
        chain.doFilter(request, response); // 다음 필터로 넘김
    }
}
package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.SessionManager;

public class SessionFilter implements Filter {
    @Override
    public void doFilter(CustomHttpRequest request, CustomHttpResponse response, FilterChain chain) {
        SessionManager.getOrCreateSession(request, response); // 세션을 항상 생성 또는 재활용
        chain.doFilter(request, response); // 다음 필터로 넘김
    }
}
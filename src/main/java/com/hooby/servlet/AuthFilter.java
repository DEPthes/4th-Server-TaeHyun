package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import com.hooby.http.Session;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(CustomHttpRequest request, CustomHttpResponse response, FilterChain chain) {
        String path = request.getPath();

        // 인증 없이 접근 가능한 경로 예외 처리
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        Session session = request.getSession();  // 💡 기존 Session 사용
        Object user = session.getAttribute("user");

        if (user == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setBody("🟠 Unauthorized: 로그인 필요");
            System.out.println("🔴 AuthFilter: 비로그인 요청 차단됨");
            return;
        }

        System.out.println("✅ AuthFilter: 로그인 사용자 통과 → " + user);
        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/login") || path.equals("/users");
    }
}
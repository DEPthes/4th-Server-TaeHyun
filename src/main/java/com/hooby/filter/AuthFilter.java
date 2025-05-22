package com.hooby.filter;

import com.hooby.aop.LoggingAdvice;
import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import com.hooby.http.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    @Override
    public void doFilter(CustomHttpRequest request, CustomHttpResponse response, FilterChain chain) {
        String path = request.getPath();

        // 인증 없이 접근 가능한 경로 예외 처리
        if (isPublicPath(path, request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        Session session = request.getSession();
        Object user = session.getAttribute("user");

        if (user == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setBody("🟠 Unauthorized: 로그인 필요");
            logger.error("🔴 AuthFilter: 비로그인 요청 차단됨");
            return;
        }

        logger.info("✅ AuthFilter: 로그인 사용자 통과 → " + user);
        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path, String method) {
        return (path.equals("/login")) ||
                (path.equals("/users") && method.equals("POST")); // 회원가입 허용
    }
}
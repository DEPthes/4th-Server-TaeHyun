package com.hooby.filter;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);
    private final SessionManager sessionManager;

    public SessionFilter(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        logger.info("🧩 생성자 주입됨: SessionManager into SessionFilter");
    }

    @Override
    public void doFilter(CustomHttpRequest request, CustomHttpResponse response, FilterChain chain) {
        sessionManager.getOrCreateSession(request, response); // 세션을 항상 생성 또는 재활용
        chain.doFilter(request, response); // 다음 필터로 넘김
    }
}
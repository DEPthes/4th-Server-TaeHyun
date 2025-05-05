package com.hooby.filter;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(CustomHttpRequest request, CustomHttpResponse response, FilterChain chain) {
        logger.info("→ [FILTER] Incoming request: {} {}", request.getMethod(), request.getPath());
        chain.doFilter(request, response);
        logger.info("← [FILTER] Response status: {}", response.toHttpMessage().split(" ")[1]);
    }
}
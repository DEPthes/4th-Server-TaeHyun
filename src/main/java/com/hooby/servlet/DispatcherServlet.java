package com.hooby.servlet;

import com.hooby.filter.FilterChain;
import com.hooby.filter.FilterManager;
import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherServlet {
    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    private final ServletMapper servletMapper;
    private final ServletInitializer servletInitializer;
    private final FilterManager filterManager;

    public DispatcherServlet(ServletMapper servletMapper,
                             ServletInitializer servletInitializer,
                             FilterManager filterManager) {
        this.servletMapper = servletMapper;
        this.servletInitializer = servletInitializer;
        this.filterManager = filterManager;

        logger.info("🧩 DispatcherServlet 생성자 주입 완료: {}, {}, {}",
                servletMapper.getClass().getSimpleName(),
                servletInitializer.getClass().getSimpleName(),
                filterManager.getClass().getSimpleName());
    }

    public CustomHttpResponse service(CustomHttpRequest request) {
        logger.debug("▶️ DispatcherServlet 요청 처리 시작: {}", request.getPath());

        CustomHttpResponse response = new CustomHttpResponse();

        ServletMapper.MappingResult result = servletMapper.map(request.getPath());
        if (result == null) {
            logger.warn("🚫 매핑 실패: {}", request.getPath());
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody("Not Found");
            return response;
        }

        request.setPathParams(result.pathParams());
        Servlet servlet = servletInitializer.getOrCreate(result.servletName());
        logger.debug("✅ 매핑된 서블릿: {}", result.servletName());

        FilterChain chain = new FilterChain(filterManager.getFilters(), servlet);
        chain.doFilter(request, response);

        logger.debug("✅ 요청 처리 완료: {} → {}", request.getPath(), response.getStatus());
        return response;
    }
}
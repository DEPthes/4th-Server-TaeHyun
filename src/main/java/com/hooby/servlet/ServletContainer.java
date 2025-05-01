package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import com.hooby.http.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContainer {
    private static final Logger logger = LoggerFactory.getLogger(ServletContainer.class);

    private final ServletMapper servletMapper;
    private final ServletInitializer servletInitializer;
    private final FilterManager filterManager;

    public ServletContainer(
            ServletMapper servletMapper,
            ServletInitializer servletInitializer,
            FilterManager filterManager
    ) {
        this.servletMapper = servletMapper;
        this.servletInitializer = servletInitializer;
        this.filterManager = filterManager;
    }

    public CustomHttpResponse dispatch(CustomHttpRequest request) {
        CustomHttpResponse response = new CustomHttpResponse();

        // 기존 세션이 있으면 재사용하고 없으면 새 세션 생성.
        SessionManager.getOrCreateSession(request, response); // JSESSIONID 존재 여부 판단

        ServletMapper.MappingResult result = servletMapper.map(request.getPath());

        if (result == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody("Not Found");
            return response;
        }

        request.setPathParams(result.pathParams());
        Servlet servlet = servletInitializer.getOrCreate(result.servletName());
//        servlet.service(request, response); // doFilter 로 service 책임을 위임함. Filter 를 거쳐야 하니까

        FilterChain chain = new FilterChain(filterManager.getFilters(), servlet);
        chain.doFilter(request, response);

        return response;
    }
}

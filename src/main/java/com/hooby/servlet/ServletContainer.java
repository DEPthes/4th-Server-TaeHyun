package com.hooby.servlet;

import com.hooby.filter.FilterChain;
import com.hooby.filter.FilterManager;
import com.hooby.http.*;
import com.hooby.listener.ListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContainer {
    private static final Logger logger = LoggerFactory.getLogger(ServletContainer.class);

    private final ServletMapper servletMapper;
    private final ServletInitializer servletInitializer;
    private final FilterManager filterManager;
    private final ListenerManager listenerManager;

    public ServletContainer(
            ServletMapper servletMapper,
            ServletInitializer servletInitializer,
            FilterManager filterManager,
            ListenerManager listenerManager
    ) {
        this.servletMapper = servletMapper;
        this.servletInitializer = servletInitializer;
        this.filterManager = filterManager;
        this.listenerManager = listenerManager;

        System.out.println("🧩 생성자 주입됨: "
                + "servletMapper=" + servletMapper.getClass().getSimpleName()
                + ", servletInitializer=" + servletInitializer.getClass().getSimpleName()
                + ", filterManager=" + filterManager.getClass().getSimpleName()
                + ", listenerManager=" + listenerManager.getClass().getSimpleName());
    }

    public CustomHttpResponse dispatch(CustomHttpRequest request) {
        CustomHttpResponse response = new CustomHttpResponse();

        ServletMapper.MappingResult result = servletMapper.map(request.getPath());

        if (result == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody("Not Found");
            return response;
        }

        request.setPathParams(result.pathParams());
        Servlet servlet = servletInitializer.getOrCreate(result.servletName());

        FilterChain chain = new FilterChain(filterManager.getFilters(), servlet);
        chain.doFilter(request, response);

        return response;
    }

    public void init() {
        System.out.println("🟢 ServletContainer 초기화됨");
        listenerManager.notifyInit();
    }

    public void cleanup() {
        System.out.println("🔴 ServletContainer 종료됨");
        listenerManager.notifyDestroy();
    }
}

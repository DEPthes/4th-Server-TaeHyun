package com.hooby.servlet;

import com.hooby.http.*;
import com.hooby.listener.ListenerManager;
import com.hooby.listener.SessionListener;
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
            FilterManager filterManager
    ) {
        this.servletMapper = servletMapper;
        this.servletInitializer = servletInitializer;
        this.filterManager = filterManager;

        // ServletContainer 내부에서 ListenerManager 생성 및 등록
        this.listenerManager = new ListenerManager();
        this.listenerManager.addSessionListener(new SessionListener() {
            @Override
            public void onSessionCreated(Session session) {
                System.out.println("🟢 Listener: 세션 생성됨 → " + session.getId());
            }

            @Override
            public void onSessionDestroyed(Session session) {
                System.out.println("🔴 Listener: 세션 제거됨 → " + session.getId());
            }
        });

        // 세션 매니저에 등록
        SessionManager.setListenerManager(listenerManager);
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
}

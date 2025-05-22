package com.hooby.servlet;

import com.hooby.filter.FilterChain;
import com.hooby.filter.FilterManager;
import com.hooby.http.*;
import com.hooby.listener.ListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContainer {
    private static final Logger logger = LoggerFactory.getLogger(ServletContainer.class);

    private final ListenerManager listenerManager;
    private final DispatcherServlet dispatcher;

    public ServletContainer(DispatcherServlet dispatcher, ListenerManager listenerManager) {
        this.dispatcher = dispatcher;
        this.listenerManager = listenerManager;
        logger.info("🧩 ServletContainer 생성자 주입됨");
    }

    public CustomHttpResponse dispatch(CustomHttpRequest request) {
        logger.debug("ServletContainer: dispatch 진입 - {}", request.getPath());
        return dispatcher.service(request);
    }

    public void init() {
        logger.info("🟢 ServletContainer 초기화됨");
        listenerManager.notifyInit();
    }

    public void cleanup() {
        logger.info("🔴 ServletContainer 종료됨");
        listenerManager.notifyDestroy();
    }
}

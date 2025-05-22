package com.hooby.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(ServletInitializer.class);
    private final Map<String, Supplier<Servlet>> servletFactories = new HashMap<>();
    private final Map<String, Servlet> servletCache = new HashMap<>();

    // DI 방식으로 서블릿들을 모두 주입받음
    public ServletInitializer(List<Servlet> servlets) {
        for (Servlet servlet : servlets) {
            String className = servlet.getClass().getSimpleName();
            String key = Character.toLowerCase(className.charAt(0)) + className.substring(1); // ex: "UserServlet" → "userServlet"
            servletFactories.put(key, () -> servlet);
        }
        logger.info("🧩 생성자 주입됨: {}", servletFactories.keySet());
    }

    public Servlet getOrCreate(String servletName) {
        return servletCache.computeIfAbsent(servletName, name -> {
            Supplier<Servlet> creator = servletFactories.get(name);
            if (creator == null) throw new IllegalArgumentException("❌ 등록된 서블릿 없음: " + name);
            return creator.get();
        });
    }

    public void init() {
        logger.info("🟢 ServletInitializer 초기화됨");
    }
    public void cleanup() {
        logger.info("🔴 ServletInitializer 종료됨");
    }
}

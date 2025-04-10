package com.hooby.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ServletInitializer {
    // 1. 등록된 서블릿 생성 함수 모음
    private final Map<String, Supplier<Servlet>> servletFactories = new HashMap<>();

    // 2. 실제 생성되어 Heap 에 올라간 서블릿 인스턴스
    private final Map<String, Servlet> servletCache = new HashMap<>();

    // 서블릿 생성 팩토리 등록
    public void registerFactory(String servletName, Supplier<Servlet> creator) {
        servletFactories.put(servletName, creator);
    }

    // 서블릿 인스턴스 반환 (없으면 초기화하여 생성 후 캐싱)
    public Servlet getOrCreate(String servletName) {
        // 이미 캐시에 있다면 반환
        if (servletCache.containsKey(servletName)) {
            return servletCache.get(servletName);
        }

        // 없으면 팩토리 함수로 생성
        Supplier<Servlet> creator = servletFactories.get(servletName);
        if (creator == null) {
            throw new IllegalArgumentException("❌ 서블릿 팩토리가 등록되지 않음: " + servletName);
        }

        Servlet servlet = creator.get();
        servletCache.put(servletName, servlet); // 캐시에 저장
        return servlet;
    }
}

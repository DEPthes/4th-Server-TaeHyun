package com.hooby.filter;

import java.util.List;

public class FilterManager {
    private final List<Filter> filters;

    public FilterManager(List<Filter> filters) {
        System.out.println("🧩 생성자 주입됨: " + filters);
        this.filters = filters;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void init() {
        System.out.println("🟢 FilterManager 초기화됨");
        for (Filter f : filters) {
            f.init();
        }
    }

    public void destroy() {
        System.out.println("🔴 FilterManager 종료됨");
        for (Filter f : filters) {
            f.destroy();
        }
    }
}
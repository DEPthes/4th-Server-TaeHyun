package com.hooby.filter;

import com.hooby.servlet.ServletInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FilterManager {
    private final List<Filter> filters;
    private static final Logger logger = LoggerFactory.getLogger(FilterManager.class);


    public FilterManager(List<Filter> filters) {
        logger.info("🧩 생성자 주입됨: {}", filters);
        this.filters = filters;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void init() {
        logger.info("🟢 FilterManager 초기화됨");
        for (Filter f : filters) {
            f.init();
        }
    }

    public void destroy() {
        logger.info("🔴 FilterManager 종료됨");
        for (Filter f : filters) {
            f.destroy();
        }
    }
}
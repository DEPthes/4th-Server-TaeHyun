package com.hooby.filter;

import java.util.ArrayList;
import java.util.List;

public class FilterManager {
    private final List<Filter> filters = new ArrayList<>();

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public List<Filter> getFilters() {
        return List.copyOf(filters);
    }
}
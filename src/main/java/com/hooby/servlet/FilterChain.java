package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;

import java.util.List;

public class FilterChain {
    private final List<Filter> filters;
    private final Servlet servlet;
    private int index = 0;

    public FilterChain(List<Filter> filters, Servlet servlet) {
        this.filters = filters;
        this.servlet = servlet;
    }

    public void doFilter(CustomHttpRequest request, CustomHttpResponse response) {
        if (index < filters.size()) {
            Filter nextFilter = filters.get(index++);
            nextFilter.doFilter(request, response, this);
        } else {
            servlet.service(request, response);
        }
    }
}
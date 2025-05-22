package com.hooby.filter;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import com.hooby.servlet.Servlet;

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
            try {
                servlet.service(request, response);
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setBody("Internal error: " + e.getMessage());
            }
        }
    }
}
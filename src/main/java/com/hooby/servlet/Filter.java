package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;

public interface Filter {
    void doFilter(CustomHttpRequest request, CustomHttpResponse response, FilterChain chain);
}
package com.hooby.filter;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;

public interface Filter {
    void doFilter(CustomHttpRequest request, CustomHttpResponse response, FilterChain chain);
    default void init() {}
    default void destroy() {}
}
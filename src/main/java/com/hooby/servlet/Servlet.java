package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;

public interface Servlet {
    void service(CustomHttpRequest request, CustomHttpResponse response);
}

package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;

public class TestServlet implements Servlet {

    @Override
    public void service(CustomHttpRequest request, CustomHttpResponse response) {
        // HTTP Method 검사
        if (!"GET".equals(request.getMethod())) {
            response.setStatus(405); // Method Not Allowed
            response.setBody("Hahaha this api needs a GET Method");
            return;
        }

        // User-Agent 헤더 확인
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "unknown client";
        }

        String responseBody = "Hello => " + userAgent ;
        response.setBody(responseBody);
    }
}

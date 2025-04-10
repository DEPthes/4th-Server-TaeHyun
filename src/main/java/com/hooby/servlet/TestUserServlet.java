package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;

public class TestUserServlet implements Servlet{
    @Override
    public void service(CustomHttpRequest request, CustomHttpResponse response) {
        String userId = request.getPathParams().get("id");
        String searchQuery = request.getQueryParams().get("q");

        String body = "User ID (from path): " + userId + "\n" +
                "Search query (from query): " + searchQuery;

        response.setStatus(200);
        response.setBody(body);
    }
}
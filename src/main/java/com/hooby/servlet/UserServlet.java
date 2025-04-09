package com.hooby.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;


public class UserServlet implements Servlet {

    // 간단한 메모리 저장소 (DB 대용)
    private final Map<String, Map<String, Object>> userDb = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void service(CustomHttpRequest request, CustomHttpResponse response) {
        switch (request.getMethod()) {
            case "GET" -> handleGet(request, response);
            case "POST" -> handlePost(request, response);
            case "PUT" -> handlePut(request, response);
            case "PATCH" -> handlePatch(request, response);
            case "DELETE" -> handleDelete(request, response);
            default -> {
                response.setStatus(405);
                response.setBody("Method Not Allowed");
            }
        }
    }

    private void handleGet(CustomHttpRequest req, CustomHttpResponse resp) {
        Map<String, Object> body = req.getJsonBody();

        try {
            String json = objectMapper.writeValueAsString(userDb.values());
            resp.setStatus(HttpStatus.OK);
            resp.setHeader("Content-Type", "application/json");
            resp.setBody(json);
        } catch (Exception e) {
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            resp.setBody("Serialization error");
        }
    }

    private void handlePost(CustomHttpRequest req, CustomHttpResponse resp) {
        Map<String, Object> body = req.getJsonBody();
        String id = (String) body.get("id");

        if (id == null || body.get("name") == null || body.get("age") == null) {
            resp.setStatus(HttpStatus.BAD_REQUEST);
            resp.setBody("Missing fields: id, name, age are required");
            return;
        }

        if (userDb.containsKey(id)) {
            resp.setStatus(HttpStatus.CONFLICT);
            resp.setBody("User already exists");
            return;
        }

        userDb.put(id, body);
        resp.setStatus(HttpStatus.CREATED);
        resp.setBody("User created");
    }

    private void handlePut(CustomHttpRequest req, CustomHttpResponse resp) {
        Map<String, Object> body = req.getJsonBody();
        String id = (String) body.get("id");

        if (id == null || body.get("name") == null || body.get("age") == null) {
            resp.setStatus(HttpStatus.BAD_REQUEST);
            resp.setBody("Missing fields: id, name, age are required");
            return;
        }

        if (!userDb.containsKey(id)) {
            resp.setStatus(HttpStatus.NOT_FOUND);
            resp.setBody("User not found");
            return;
        }

        userDb.put(id, body);
        resp.setStatus(HttpStatus.OK);
        resp.setBody("User updated");
    }

    private void handlePatch(CustomHttpRequest req, CustomHttpResponse resp) {
        Map<String, Object> body = req.getJsonBody();
        String id = (String) body.get("id");

        if (id == null || !userDb.containsKey(id)) {
            resp.setStatus(HttpStatus.NOT_FOUND);
            resp.setBody("User not found");
            return;
        }

        Map<String, Object> user = userDb.get(id);
        body.forEach((key, value) -> {
            if (!"id".equals(key)) {
                user.put(key, value);
            }
        });

        resp.setStatus(HttpStatus.OK);
        resp.setBody("User partially updated");
    }

    private void handleDelete(CustomHttpRequest req, CustomHttpResponse resp) {
        userDb.clear();
        resp.setStatus(HttpStatus.OK);
        resp.setBody("All users deleted");
    }
}

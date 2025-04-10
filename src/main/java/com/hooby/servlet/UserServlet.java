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
    public void service(CustomHttpRequest req, CustomHttpResponse res) {
        String id = req.getPathParams().get("id"); // id 에 매칭되는 값을 저장
        String method = req.getMethod();
        try{
            switch (req.getMethod()) {
                case "GET" -> {
                    if (id == null) getAllUsers(req, res);
                    else getUserById(id, res);
                }
                case "POST" -> createUser(req, res);
                case "PUT" -> updateUser(id, req, res);
                case "PATCH" -> patchUser(id, req, res);
                case "DELETE" -> {
                    if (id == null) deleteAllUsers(res);
                    else deleteUserById(id, res);
                }
                default -> {
                    res.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
                    res.setBody("Method Not Allowed");
                }
            }
        } catch (Exception e){
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            res.setBody("Internal Server Error: " + e.getMessage());
        }
    }

    // 여기에 GetMapping 어노테이션 이런거 달고 싶다..
    private void getAllUsers(CustomHttpRequest req, CustomHttpResponse res) throws Exception {
        String q = req.getQueryParams().get("q");
        String ageFilter = req.getQueryParams().get("age");

        System.out.println("DEBUG: query q = " + q);

        var result = userDb.values().stream()
                .filter(user -> q == null || user.get("name").toString().contains(q))
                .filter(user -> ageFilter == null || user.get("age").toString().equals(ageFilter))
                .toList();

        String json = objectMapper.writeValueAsString(result);
        res.setStatus(HttpStatus.OK);
        res.setHeader("Content-Type", "application/json");
        res.setBody(json);
    }

    private void getUserById(String id, CustomHttpResponse res) throws Exception {
        if(!userDb.containsKey(id)) {
            res.setStatus(HttpStatus.NOT_FOUND);
            res.setBody("User not found" + id);
            return;
        }
        String json = objectMapper.writeValueAsString(userDb.get(id));
        res.setStatus(HttpStatus.OK);
        res.setHeader("Content-Type", "application/json");
        res.setBody(json);
    }

    private void createUser(CustomHttpRequest req, CustomHttpResponse res){
        Map<String, Object> body = req.getJsonBody();
        String id = (String) body.get("id");

        if (id == null || body.get("name") == null || body.get("age") == null) {
            res.setStatus(HttpStatus.BAD_REQUEST);
            res.setBody("Missing fields: id, name, age");
            return;
        }

        if (userDb.containsKey(id)) {
            res.setStatus(HttpStatus.CONFLICT);
            res.setBody("User already exists");
            return;
        }

        userDb.put(id, body);
        res.setStatus(HttpStatus.CREATED);
        res.setBody("User created");
    }

    private void updateUser(String id, CustomHttpRequest req, CustomHttpResponse res) {
        if (id == null || !userDb.containsKey(id)) {
            res.setStatus(HttpStatus.NOT_FOUND);
            res.setBody("User not found");
            return;
        }

        Map<String, Object> body = req.getJsonBody();
        if (body.get("name") == null || body.get("age") == null) {
            res.setStatus(HttpStatus.BAD_REQUEST);
            res.setBody("Missing fields: name, age");
            return;
        }

        body.put("id", id); // URI 기준 ID 강제 고정
        userDb.put(id, body);
        res.setStatus(HttpStatus.OK);
        res.setBody("User updated");
    }

    private void patchUser(String id, CustomHttpRequest req, CustomHttpResponse res) {
        if (id == null || !userDb.containsKey(id)) {
            res.setStatus(HttpStatus.NOT_FOUND);
            res.setBody("User not found");
            return;
        }

        Map<String, Object> user = userDb.get(id);
        Map<String, Object> body = req.getJsonBody();
        body.forEach((key, value) -> {
            if (!"id".equals(key)) {
                user.put(key, value);
            }
        });

        res.setStatus(HttpStatus.OK);
        res.setBody("User partially updated");
    }

    private void deleteAllUsers(CustomHttpResponse res) {
        userDb.clear();
        res.setStatus(HttpStatus.OK);
        res.setBody("All users deleted");
    }

    private void deleteUserById(String id, CustomHttpResponse res) {
        if (!userDb.containsKey(id)) {
            res.setStatus(HttpStatus.NOT_FOUND);
            res.setBody("User not found");
            return;
        }

        userDb.remove(id);
        res.setStatus(HttpStatus.OK);
        res.setBody("User deleted");
    }
}
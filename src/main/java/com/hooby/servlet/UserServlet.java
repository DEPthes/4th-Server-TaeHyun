package com.hooby.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hooby.http.*;
import com.hooby.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.NoSuchElementException;

public class UserServlet implements Servlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void service(CustomHttpRequest req, CustomHttpResponse res) {
        String id = req.getPathParams().get("id");
        String path = req.getPath();

        try {
            switch (req.getMethod()) {
                case "GET" -> {
                    if (id == null) getAllUsers(req, res);
                    else getUserById(id, res);
                }
                case "POST" -> {
                    if ("/login".equals(path)) {
                        handleLogin(req, res);
                    } else if ("/users/fail".equals(path)) {
                        createUserWithFailure(req, res); // 트랜잭션 테스트용 추가
                    } else {
                        createUser(req, res);
                    }
                }
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
        } catch (Exception e) {
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            res.setBody("Internal Server Error: " + e.getMessage());
        }
    }

    private void createUser(CustomHttpRequest req, CustomHttpResponse res) {
        Map<String, Object> body = req.getJsonBody();
        try {
            userService.createUser(body);
            res.setStatus(HttpStatus.CREATED);
            res.setBody("User created");
        } catch (IllegalArgumentException e) {
            res.setStatus(HttpStatus.BAD_REQUEST);
            res.setBody(e.getMessage());
        } catch (RuntimeException e) {
            res.setStatus(HttpStatus.CONFLICT);
            res.setBody(e.getMessage());
        }
    }

    // 트랜잭션 테스트용 추가 메서드
    private void createUserWithFailure(CustomHttpRequest req, CustomHttpResponse res) {
        Map<String, Object> body = req.getJsonBody();
        try {
            userService.createUserWithException(body); // 여기로 가면 서비스 로직이 있는데, 여기서 아예 등록을 한 후 에러를 내버리자.
            res.setStatus(HttpStatus.CREATED);
            res.setBody("User created");
        } catch (RuntimeException e) {  // UserServiceImpl 에서 fail 이란 id 를 넣어서 예외가 발생하고
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR); // 500 과 함꼐
            res.setBody("예외 발생 → 트랜잭션 롤백됨: " + e.getMessage()); // 이게 출력됨. 근데 이게 나왔다고 롤백이 된건 아니고...
        }
    }

    private void getAllUsers(CustomHttpRequest req, CustomHttpResponse res) throws Exception {
        String nameFilter = req.getQueryParams().get("q");
        String ageFilter = req.getQueryParams().get("age");
        var result = userService.getAllUsers(nameFilter, ageFilter);
        res.setStatus(HttpStatus.OK);
        res.setHeader("Content-Type", "application/json");
        res.setBody(objectMapper.writeValueAsString(result));
    }

    private void getUserById(String id, CustomHttpResponse res) throws Exception {
        if (checkIdisNull(id, res)) return;
        var user = userService.getUserById(id);
        if (user == null) {
            res.setStatus(HttpStatus.NOT_FOUND);
            res.setBody("User not found");
            return;
        }
        res.setStatus(HttpStatus.OK);
        res.setHeader("Content-Type", "application/json");
        res.setBody(objectMapper.writeValueAsString(user));
    }

    private void updateUser(String id, CustomHttpRequest req, CustomHttpResponse res) {
        if (checkIdisNull(id, res)) return;

        try {
            userService.updateUser(id, req.getJsonBody());
            res.setStatus(HttpStatus.OK);
            res.setBody("User updated");
        } catch (NoSuchElementException e) {
            res.setStatus(HttpStatus.NOT_FOUND);
            res.setBody(e.getMessage());
        } catch (IllegalArgumentException e) {
            res.setStatus(HttpStatus.BAD_REQUEST);
            res.setBody(e.getMessage());
        }
    }

    private void patchUser(String id, CustomHttpRequest req, CustomHttpResponse res) {
        if (checkIdisNull(id, res)) return;

        try {
            userService.patchUser(id, req.getJsonBody());
            res.setStatus(HttpStatus.OK);
            res.setBody("User partially updated");
        } catch (NoSuchElementException e) {
            res.setStatus(HttpStatus.NOT_FOUND);
            res.setBody(e.getMessage());
        }
    }

    private void deleteUserById(String id, CustomHttpResponse res) {
        if (checkIdisNull(id, res)) return;

        try {
            userService.deleteUser(id);
            res.setStatus(HttpStatus.OK);
            res.setBody("User deleted");
        } catch (NoSuchElementException e) {
            res.setStatus(HttpStatus.NOT_FOUND);
            res.setBody(e.getMessage());
        }
    }

    private void deleteAllUsers(CustomHttpResponse res) {
        userService.deleteAllUsers();
        res.setStatus(HttpStatus.OK);
        res.setBody("All users deleted");
    }

    private void handleLogin(CustomHttpRequest req, CustomHttpResponse res) throws Exception {
        Map<String, Object> body = req.getJsonBody();
        String id = (String) body.get("id");

        if (id == null || !userService.login(id)) {
            res.setStatus(HttpStatus.UNAUTHORIZED);
            res.setBody("Invalid login ID");
            return;
        }

        Session session = req.getSession();
        session.setAttribute("user", id);

        res.setStatus(HttpStatus.OK);
        res.setBody("Login successful");
    }

    public void init() {
        logger.info("🟢 UserServlet 초기화됨");
    }

    public void cleanup() {
        logger.info("🔴 UserServlet 자원 해제됨");
    }

    private boolean checkIdisNull(String id, CustomHttpResponse res) {
        if (id == null) {
            res.setStatus(HttpStatus.BAD_REQUEST);
            res.setBody("ID is required");
            return true;
        }
        return false;
    }
}
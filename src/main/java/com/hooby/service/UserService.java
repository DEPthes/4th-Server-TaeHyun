package com.hooby.service;

import java.util.List;
import java.util.Map;

public interface UserService {
    void createUser(Map<String, Object> user);
    List<Map<String, Object>> getAllUsers(String nameFilter, String ageFilter);
    Map<String, Object> getUserById(String id);
    void updateUser(String id, Map<String, Object> newUser);
    void patchUser(String id, Map<String, Object> patchData);
    void deleteUser(String id);
    void deleteAllUsers();
    boolean login(String id);
    void createUserWithException(Map<String, Object> user);
}
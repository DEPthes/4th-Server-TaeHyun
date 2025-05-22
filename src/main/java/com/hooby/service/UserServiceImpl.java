package com.hooby.service;

import com.hooby.dao.UserDao;

import java.util.*;

public class UserServiceImpl implements UserService {
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void createUser(Map<String, Object> user) {
        validateUser(user);
        userDao.insertUser(user);
    }

    @Override
    public void createUserWithException(Map<String, Object> user) {
        validateUser(user);
        userDao.insertUser(user);
        if ("fail".equals(user.get("id"))) {
            throw new RuntimeException("강제 예외 발생");
        }
    }

    @Override
    public List<Map<String, Object>> getAllUsers(String nameFilter, String ageFilter) {
        return userDao.selectUsers(nameFilter, ageFilter);
    }

    @Override
    public Map<String, Object> getUserById(String id) {
        Map<String, Object> user = userDao.selectUserById(id);
        if (user == null) throw new NoSuchElementException("User not found");
        return user;
    }

    @Override
    public void updateUser(String id, Map<String, Object> newUser) {
        validateUser(newUser);
        int affected = userDao.updateUser(id, newUser);
        if (affected == 0) throw new NoSuchElementException("User not found");
    }

    @Override
    public void patchUser(String id, Map<String, Object> patchData) {
        Map<String, Object> existing = userDao.selectUserById(id);
        if (existing == null) throw new NoSuchElementException("User not found");

        String name = patchData.containsKey("name") ? (String) patchData.get("name") : (String) existing.get("name");
        int age = patchData.containsKey("age") ? (Integer) patchData.get("age") : (Integer) existing.get("age");

        int affected = userDao.updateUserNameAndAge(id, name, age);
        if (affected == 0) throw new NoSuchElementException("User not found");
    }

    @Override
    public void deleteUser(String id) {
        int affected = userDao.deleteUser(id);
        if (affected == 0) throw new NoSuchElementException("User not found");
    }

    @Override
    public void deleteAllUsers() {
        userDao.deleteAll();
    }

    @Override
    public boolean login(String id) {
        return userDao.selectUserById(id) != null;
    }

    private void validateUser(Map<String, Object> user) {
        if (user.get("id") == null || user.get("name") == null || user.get("age") == null) {
            throw new IllegalArgumentException("사용자 정보가 불완전합니다");
        }
    }
}
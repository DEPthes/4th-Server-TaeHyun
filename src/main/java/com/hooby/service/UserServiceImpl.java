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
        userDao.insertUser(user);
    }

    // 트랜잭션 롤백 시나리오
    @Override
    public void createUserWithException(Map<String, Object> user) {
        userDao.insertUser(user);
        if ("fail".equals(user.get("id"))) throw new RuntimeException("강제 예외 발생");
    }

    @Override
    public List<Map<String, Object>> getAllUsers(String nameFilter, String ageFilter) {
        return userDao.selectUsers(nameFilter, ageFilter);
    }

    @Override
    public Map<String, Object> getUserById(String id) {
        return userDao.selectUserById(id);
    }

    @Override
    public void updateUser(String id, Map<String, Object> newUser) {
        userDao.updateUser(id, newUser);
    }

    @Override
    public void patchUser(String id, Map<String, Object> patchData) {
        userDao.patchUser(id, patchData);
    }

    @Override
    public void deleteUser(String id) {
        userDao.deleteUser(id);
    }

    @Override
    public void deleteAllUsers() {
        userDao.deleteAll();
    }

    @Override
    public boolean login(String id) {
        return userDao.selectUserById(id) != null;
    }
}
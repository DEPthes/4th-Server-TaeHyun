package com.hooby.service;

import com.hooby.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    // 생성자 기반 주입
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String serve() {
        System.out.println("UserService is serving → " + userRepository.findUser());
        return "UserService is serving → " + userRepository.findUser();
    }

    public void init() {
        System.out.println("🟢 UserService 초기화됨");
    }

    public void cleanup() {
        System.out.println("🔴 UserService 자원 해제됨");
    }
}
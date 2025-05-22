package com.hooby.service;

import com.hooby.listener.LoggingSessionListener;
import com.hooby.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    // 생성자 기반 주입
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String serve() {
        logger.info("UserService is serving → {}", userRepository.findUser());
        return "UserService is serving → " + userRepository.findUser();
    }

    public void init() { logger.info("🟢 UserService 초기화됨"); }

    public void cleanup() {
        logger.info("🔴 UserService 자원 해제됨");
    }
}
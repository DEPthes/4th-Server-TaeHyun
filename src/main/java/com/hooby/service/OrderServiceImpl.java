package com.hooby.service;

import com.hooby.aop.LoggingAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Override
    public void createOrder(Map<String, Object> order) {
        if (order.get("item") == null || order.get("amount") == null) {
            throw new IllegalArgumentException("Missing required order fields");
        }
        logger.info("Order 생성: {}", order);
    }
}
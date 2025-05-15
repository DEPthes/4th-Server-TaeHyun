package com.hooby.service;

import java.util.Map;

public class OrderServiceImpl implements OrderService {
    @Override
    public void createOrder(Map<String, Object> order) {
        System.out.println("Order 생성: " + order);
    }
}
package com.hooby.service;

public class PaymentServiceImpl implements PaymentService {
    @Override
    public void pay(String userId, int amount) {
        System.out.println("결제 처리: " + userId + ", " + amount);
    }
}
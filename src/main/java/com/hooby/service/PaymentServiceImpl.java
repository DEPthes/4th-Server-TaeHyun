package com.hooby.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    @Override
    public void pay(String userId, int amount) {
        logger.info("결제 처리: {}, {}", userId, amount);
    }
}
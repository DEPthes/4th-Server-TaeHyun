package com.hooby.servlet;

import com.hooby.http.*;
import com.hooby.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentServlet implements Servlet {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServlet.class);
    private PaymentService paymentService;
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void service(CustomHttpRequest req, CustomHttpResponse res) {
        try {
            String user = req.getQueryParams().get("user");
            String amountStr = req.getQueryParams().get("amount");
            if (user == null || amountStr == null) throw new IllegalArgumentException("Missing parameters");

            int amount = Integer.parseInt(amountStr);
            paymentService.pay(user, amount);

            res.setStatus(HttpStatus.OK);
            res.setBody("Payment Done");
        } catch (Exception e) {
            res.setStatus(HttpStatus.BAD_REQUEST);
            res.setBody("Invalid request: " + e.getMessage());
        }
    }

    public void init() {
        logger.info("🟢 PaymentServlet 초기화됨");
    }

    public void cleanup() { logger.info("🔴 PaymentServlet 자원 해제됨"); }
}
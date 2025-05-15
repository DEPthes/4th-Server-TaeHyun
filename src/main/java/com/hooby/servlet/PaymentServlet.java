package com.hooby.servlet;

import com.hooby.http.*;
import com.hooby.service.PaymentService;

public class PaymentServlet implements Servlet {

    private PaymentService paymentService;

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void service(CustomHttpRequest req, CustomHttpResponse res) {
        String user = req.getQueryParams().get("user");
        int amount = Integer.parseInt(req.getQueryParams().get("amount"));

        paymentService.pay(user, amount);
        res.setStatus(HttpStatus.OK);
        res.setBody("Payment Done");
    }

    public void init() {
        System.out.println("🟢 PaymentServlet 초기화됨");
    }

    public void cleanup() {
        System.out.println("🔴 PaymentServlet 자원 해제됨");
    }
}
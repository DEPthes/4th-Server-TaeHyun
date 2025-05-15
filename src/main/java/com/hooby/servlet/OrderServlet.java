package com.hooby.servlet;

import com.hooby.http.*;
import com.hooby.service.
        OrderService;

import java.util.Map;

public class OrderServlet implements Servlet {

    private OrderService orderService;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void service(CustomHttpRequest req, CustomHttpResponse res) {
        Map<String, Object> body = req.getJsonBody();
        orderService.createOrder(body);
        res.setStatus(HttpStatus.CREATED);
        res.setBody("Order Created");
    }

    public void init() {
        System.out.println("🟢 OrderServlet 초기화됨");
    }

    public void cleanup() {
        System.out.println("🔴 OrderServlet 자원 해제됨");
    }
}
package com.hooby.servlet;

import com.hooby.http.*;
import com.hooby.service.
        OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OrderServlet implements Servlet {
    private static final Logger logger = LoggerFactory.getLogger(OrderServlet.class);
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
        logger.info("🟢 OrderServlet 초기화됨");
    }

    public void cleanup() { logger.info("🔴 OrderServlet 자원 해제됨"); }
}
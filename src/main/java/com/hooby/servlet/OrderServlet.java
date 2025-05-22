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
        Object id = body.get("id");
        Object item = body.get("item");
        Object amount = body.get("amount");
        if (id == null || item == null || amount == null) {
            res.setStatus(HttpStatus.BAD_REQUEST);
            res.setBody("Missing required order fields");
            return;
        }
        logger.info("주문 요청 - 사용자: {}, 상품: {}, 수량: {}", id, item, amount);
        orderService.createOrder(body);

        res.setStatus(HttpStatus.CREATED);
        res.setBody("Order Created");
    }

    public void init() {
        logger.info("🟢 OrderServlet 초기화됨");
    }

    public void cleanup() { logger.info("🔴 OrderServlet 자원 해제됨"); }
}
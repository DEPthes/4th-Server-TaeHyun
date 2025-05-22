package com.hooby.aop;

import com.hooby.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAdvice implements AopAdvice {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAdvice.class);
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        logger.info("[AOP] Before method: {}", invocation.getMethod().getName());
        try {
            Object result = invocation.proceed();
            logger.info("[AOP] After method: {}", invocation.getMethod().getName());
            return result;
        } catch (Throwable t) {
            Throwable root = (t.getCause() != null) ? t.getCause() : t;
            logger.error("Exception in method: {}, reason: {}", invocation.getMethod().getName(), root.getMessage());
            throw root;
        }
    }
}
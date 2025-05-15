package com.hooby.aop;

public class LoggingAdvice implements AopAdvice {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("[AOP] Before method: " + invocation.getMethod().getName());
        try {
            Object result = invocation.proceed();
            System.out.println("[AOP] After method: " + invocation.getMethod().getName());
            return result;
        } catch (Throwable t) {
            System.out.println("❗ Exception in method: " + invocation.getMethod().getName());
            throw t;
        }
    }
}
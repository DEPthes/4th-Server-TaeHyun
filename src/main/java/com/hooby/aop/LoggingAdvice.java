package com.hooby.aop;

import java.lang.reflect.Method;

public class LoggingAdvice implements AopAdvice {
    @Override
    public Object invoke(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("🔍 [AOP] Before method: " + method.getName());
        try {
            Object result = method.invoke(target, args);
            System.out.println("✅ [AOP] After method: " + method.getName());
            return result;
        } catch (Throwable ex) {
            System.out.println("❌ [AOP] Exception in method: " + method.getName());
            throw ex;
        }
    }
}
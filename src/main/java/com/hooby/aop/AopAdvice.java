package com.hooby.aop;

import java.lang.reflect.Method;

public interface AopAdvice {
    Object invoke(Method method, Object[] args, Object target) throws Throwable;
}
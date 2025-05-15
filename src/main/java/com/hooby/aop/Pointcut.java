package com.hooby.aop;

import java.lang.reflect.Method;

public interface Pointcut {
    boolean matches(Method method, Class<?> targetClass);

    default boolean matchesAnyMethodOf(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (matches(method, clazz)) return true;
        }
        return false;
    }
}
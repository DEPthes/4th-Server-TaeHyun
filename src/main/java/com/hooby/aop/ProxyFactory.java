package com.hooby.aop;

import java.lang.reflect.*;

public class ProxyFactory {
    public static Object createProxy(Object target, Advisor advisor) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    if (advisor.getPointcut().matches(method, target.getClass())) {
                        return advisor.getAdvice().invoke(method, args, target);
                    } else {
                        return method.invoke(target, args);
                    }
                }
        );
    }
}
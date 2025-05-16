package com.hooby.aop;

import java.lang.reflect.*;
import java.util.List;

public class ProxyFactory {

    public static Object createProxy(Object target, List<AopAdvice> advices) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),           // p0: 클래스 로더
                target.getClass().getInterfaces(),           // p1: 구현할 인터페이스
                (proxy, method, args) -> {                   // p2: Invocation 핸들러
                    MethodInvocation invocation = new ReflectiveMethodInvocation(target, method, args, advices);
                    return invocation.proceed();
                }
        );
    }
}
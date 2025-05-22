package com.hooby.aop;

import java.lang.reflect.*;
import java.util.List;

public class ProxyFactory {

    public static Object createProxy(Object target, List<AopAdvice> advices) {
        try {
            Class<?> targetClass = target.getClass();
            ClassLoader classLoader = targetClass.getClassLoader();
            Class<?>[] interfaces = targetClass.getInterfaces();

            if (interfaces.length == 0) {
                throw new IllegalArgumentException("❌ 프록시 생성 실패: " + targetClass.getName() + "은(는) 인터페이스를 구현하지 않음");
            }

            return Proxy.newProxyInstance(
                    classLoader,
                    interfaces, // 구현할 인터페이스
                    (proxy, method, args) -> { // invocation handler
                        MethodInvocation invocation = new ReflectiveMethodInvocation(target, method, args, advices);
                        return invocation.proceed();
                    }
            );
        } catch (IllegalArgumentException | ClassCastException | NullPointerException e) {
            throw new RuntimeException("❌ 동적 프록시 생성 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("❌ 알 수 없는 프록시 생성 오류 발생", e);
        }
    }
}
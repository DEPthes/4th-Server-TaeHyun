package com.hooby.ioc;

import com.hooby.aop.*;

import java.lang.reflect.Method;
import java.util.*;

public class ProxyBeanPostProcessor {

    private final List<Advisor> advisors = new ArrayList<>();

    public void addAdvisor(Advisor advisor) {
        advisors.add(advisor);
    }

    public Object postProcess(Object bean) {
        Class<?> clazz = bean.getClass();

        for (Advisor advisor : advisors) {
            for (Class<?> iface : clazz.getInterfaces()) {
                for (Method m : iface.getDeclaredMethods()) {
                    if (advisor.getPointcut().matches(m, clazz)) {
                        return ProxyFactory.createProxy(bean, advisor);
                    }
                }
            }
        }

        return bean;
    }
}
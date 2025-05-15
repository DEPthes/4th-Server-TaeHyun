// Path: com.hooby.ioc.BeanPostProcessor.java
package com.hooby.ioc;

import com.hooby.aop.*;

import java.util.*;

public class BeanPostProcessor {

    private final List<Advisor> advisors = new ArrayList<>();

    public void addAdvisor(Advisor advisor) {
        advisors.add(advisor);
    }

    public Object postProcessBeforeInitialization(Object bean) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean) {
        Class<?> targetClass = bean.getClass();

        List<AopAdvice> matchedAdvices = new ArrayList<>();
        for (Advisor advisor : advisors) {
            if (advisor.getPointcut().matchesAnyMethodOf(targetClass)) {
                matchedAdvices.add(advisor.getAdvice());
            }
        }

        if (matchedAdvices.isEmpty()) return bean;

        // 🔄 프록시 생성 책임을 ProxyFactory 로 위임
        return ProxyFactory.createProxy(bean, matchedAdvices);
    }

    // 이 메서드를 통해 외부에서 Advisor 목록을 설정 가능
    public List<Advisor> getAdvisors() {
        return advisors;
    }
}
package com.hooby.ioc;

import com.hooby.aop.*;

import java.util.*;

public class BeanPostProcessor {

    private final List<Advisor> advisors = new ArrayList<>();

    public void addAdvisor(Advisor advisor) {
        advisors.add(advisor);
    }

    // 빈 초기화 완료 직후
    public Object postProcess(Object bean) {
        Class<?> targetClass = bean.getClass(); // 타겟 클래스 잡고

        List<AopAdvice> matchedAdvices = new ArrayList<>();
        for (Advisor advisor : advisors) {
            if (advisor.getPointcut().matchesAnyMethodOf(targetClass)) { // Pointcut 매칭해서
                matchedAdvices.add(advisor.getAdvice()); // 모아줌
            }
        }

        if (matchedAdvices.isEmpty()) return bean; // 없으면 그냥 그대로 빈 반환하는거고

        return ProxyFactory.createProxy(bean, matchedAdvices); // 프록시로 내보내서 생성하고 그걸 반환한다.
    }
}
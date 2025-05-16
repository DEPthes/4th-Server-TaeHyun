package com.hooby.aop;

import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveMethodInvocation implements MethodInvocation {
    private final Object target;
    private final Method method;
    private final Object[] args;
    private final List<AopAdvice> adviceChain;
    private int currentAdviceIndex = -1;

    public ReflectiveMethodInvocation(Object target, Method method, Object[] args, List<AopAdvice> adviceChain) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.adviceChain = adviceChain;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return args;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object proceed() throws Throwable {
        if (++currentAdviceIndex == adviceChain.size()) { // 어드바이스 다 돌려
            return method.invoke(target, args); // 실제 비즈니스 로직 메서드 호출
        }
        return adviceChain.get(currentAdviceIndex).invoke(this); // 다음 advice 실행
    }
}
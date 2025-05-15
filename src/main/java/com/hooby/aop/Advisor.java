package com.hooby.aop;

public class Advisor {
    private final Pointcut pointcut;
    private final AopAdvice advice;

    public Advisor(Pointcut pointcut, AopAdvice advice) {
        this.pointcut = pointcut;
        this.advice = advice;
    }

    public Pointcut getPointcut() {
        return pointcut;
    }

    public AopAdvice getAdvice() {
        return advice;
    }
}
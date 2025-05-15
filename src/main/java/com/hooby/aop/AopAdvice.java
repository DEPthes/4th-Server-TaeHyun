package com.hooby.aop;

public interface AopAdvice {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
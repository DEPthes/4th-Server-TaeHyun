package com.hooby.aop;

import com.hooby.tx.TransactionManager;

public class TransactionAdvice implements AopAdvice {
    private final TransactionManager txManager;

    public TransactionAdvice(TransactionManager txManager) {
        this.txManager = txManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        txManager.begin();
        try {
            Object result = invocation.proceed();
            txManager.commit();
            return result;
        } catch (Throwable t) {
            txManager.rollback();
            throw t;
        } finally {
            txManager.close();
        }
    }
}
package com.hooby.aop;

import com.hooby.tx.TransactionManager;
import java.lang.reflect.Method;

public class TransactionAdvice implements AopAdvice {

    private final TransactionManager txManager;

    public TransactionAdvice(TransactionManager txManager) {
        this.txManager = txManager;
    }

    @Override
    public Object invoke(Method method, Object[] args, Object target) throws Throwable {
        txManager.begin();
        try {
            Object result = method.invoke(target, args);
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
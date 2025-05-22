package com.hooby.ioc;

public interface ApplicationContext {
    Object getBean(String id);
    void close();

    // AOP 처리용 확장 포인트 등록 지원
    void addPostProcessor(BeanPostProcessor processor);
}

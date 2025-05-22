package com.hooby.ioc;

public interface BeanFactory {
    Object getBean(String id);
    void close();

    void addPostProcessor(BeanPostProcessor processor); // 공통 인터페이스에 포함시킴
}
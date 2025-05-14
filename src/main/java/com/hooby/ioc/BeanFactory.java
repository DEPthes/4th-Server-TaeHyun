package com.hooby.ioc;

public interface BeanFactory {
    Object getBean(String id);
    void close();
}
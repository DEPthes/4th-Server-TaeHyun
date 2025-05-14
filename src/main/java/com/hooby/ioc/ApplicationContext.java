package com.hooby.ioc;

public interface ApplicationContext {
    Object getBean(String id);
    void close();
}

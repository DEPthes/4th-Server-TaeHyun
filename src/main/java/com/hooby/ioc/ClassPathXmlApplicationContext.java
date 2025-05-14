package com.hooby.ioc;

public class ApplicationContext extends SimpleBeanFactory {

    public ApplicationContext(String xmlPath) {
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);
        reader.loadBeanDefinitions(xmlPath);
    }

    // getBean(), close()는 상속받음
}
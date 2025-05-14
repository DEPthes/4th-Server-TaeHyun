package com.hooby.ioc;

public class ApplicationContext {
    private final SimpleBeanFactory factory = new SimpleBeanFactory();

    public ApplicationContext(String xmlPath) {
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(xmlPath);
    }

    public Object getBean(String id) {
        return factory.getBean(id);
    }

    public void close() {
        factory.close();
    }
}
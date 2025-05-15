package com.hooby.ioc;

public class ClassPathXmlApplicationContext extends SimpleBeanFactory implements ApplicationContext {

    public ClassPathXmlApplicationContext(String... xmlPaths) {
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this); // 팩토리를 넣어서
        for (String path : xmlPaths) {
            reader.loadBeanDefinitions(path); // 빈 생성하고 내부로직에서 팩토리에 빈 등록
        }
    }

    @Override
    public Object getBean(String id) {
        return super.getBean(id); // SimpleBeanFactory에 정의되어 있으면 그대로 위임
    }

    @Override
    public void close() {
        super.close(); // 역시 위임 가능
    }
}
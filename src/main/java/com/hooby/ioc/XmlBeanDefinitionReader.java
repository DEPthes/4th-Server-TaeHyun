package com.hooby.ioc;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class XmlBeanDefinitionReader {
    private final SimpleBeanFactory factory;

    public XmlBeanDefinitionReader(SimpleBeanFactory factory) {
        this.factory = factory;
    }

    public void loadBeanDefinitions(String path) {
        try {
            // classpath resource to binaryStream
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            if (is == null) throw new FileNotFoundException("Classpath resource not found: " + path);

            // xml parsing : => XML 을 DOM 객체로 Parsing 하겠다. 즉, 파일을 객체모델로 변환하겠다. 역직렬화 느낌?
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

            // parsing <import/> tag for reader delegator
            NodeList importTags = doc.getElementsByTagName("import");
            for (int i = 0; i < importTags.getLength(); i++) {
                Element importElem = (Element) importTags.item(i);
                String resource = importElem.getAttribute("resource");
                if (!resource.isEmpty()) {
                    loadBeanDefinitions(resource); // assemble
                }
            }

            // parsing beans
            NodeList beans = doc.getElementsByTagName("bean");

            for (int i = 0; i < beans.getLength(); i++) {
                Element beanElem = (Element) beans.item(i); // export each bean
                BeanDefinition def = parseBeanDefinition(beanElem); // Create BeanDefinition obj by parsed info
                factory.registerBeanDefinition(def); // register BeanDefinition obj to BeanFactoryImpl
            }

        } catch (Exception e) {
            throw new RuntimeException("XML 파싱 실패", e);
        }
    }

    // bean 태그 하나를 BeanDefinition 으로 파싱
    private BeanDefinition parseBeanDefinition(Element beanElem) throws Exception {
        // parsing BeanDefinition fields
        String id = beanElem.getAttribute("id");
        String className = beanElem.getAttribute("class");
        String initMethod = beanElem.getAttribute("init-method");
        String destroyMethod = beanElem.getAttribute("destroy-method");

        // Define BeanDefinition Object
        BeanDefinition def = new BeanDefinition(
                id,
                className,
                initMethod.isEmpty() ? null : initMethod,
                destroyMethod.isEmpty() ? null : destroyMethod
        );

        // constructor-arg 처리
        parseConstructorArgs(beanElem, def); // 안에서 bean

        // property 처리
        parseProperties(beanElem, def);

        return def;
    }

    // <constructor-arg> 태그 처리
    private void parseConstructorArgs(Element beanElem, BeanDefinition def) throws Exception {
        NodeList ctorArgs = beanElem.getElementsByTagName("constructor-arg"); // Identify(?) Constructor DI tag
        for (int j = 0; j < ctorArgs.getLength(); j++) { // args 가 되는 dependencies 들을 ctorArgs 로 지정
            Element arg = (Element) ctorArgs.item(j); // export each a dependency || map, list

            /* Dependency Injection Logic Automatically (일단 논리적 구조는 만들어야 자동으로 만들던가 함) */

            // <map>
            NodeList mapTags = arg.getElementsByTagName("map"); // Map Case (참조 : servletMapper.xml)
            if (mapTags.getLength() > 0) {
                Map<String, String> map = parseMap((Element) mapTags.item(0));
                def.addConstructorArg(map);
                continue;
            }

            // <list>
            NodeList listTags = arg.getElementsByTagName("list"); // 참조 (그냥 아무 거나 xml 드가도 이 경우 있을 듯)
            if (listTags.getLength() > 0) {
                List<Object> list = parseList((Element) listTags.item(0));
                def.addConstructorArg(list);
                continue;
            }

            // ref 속성
            if (arg.hasAttribute("ref")) {
                def.addConstructorArg(arg.getAttribute("ref"));
            }
        }
    }

    // <property> 태그 처리
    private void parseProperties(Element beanElem, BeanDefinition def) throws Exception {
        NodeList props = beanElem.getElementsByTagName("property");
        for (int j = 0; j < props.getLength(); j++) {
            Element prop = (Element) props.item(j);
            String name = prop.getAttribute("name");

            // PropertyValue 는 그냥 분리된 값을 객체로 묶어 던질 용도로 만든 object
            if (prop.hasAttribute("ref")) {
                def.addProperty(new PropertyValue(name, prop.getAttribute("ref")));
                continue;
            }


            // child 에서 map 이나 list tag 식별
            NodeList children = prop.getChildNodes();
            for (int k = 0; k < children.getLength(); k++) {
                if (!(children.item(k) instanceof Element)) continue;
                Element child = (Element) children.item(k);

                // 위에서 처럼 각 케이스로 분리해도 되고 switch 로 처리해도 되고 ~
                switch (child.getTagName()) {
                    case "map" -> {
                        Map<String, String> map = parseMap(child);
                        def.addProperty(new PropertyValue(name, map));
                    }
                    case "list" -> {
                        List<Object> list = parseList(child);
                        def.addProperty(new PropertyValue(name, list));
                    }
                }
            }
        }
    }

    // <map> 파싱 유틸
    private Map<String, String> parseMap(Element mapElem) throws Exception {
        // LinkedHashMap 으로 하면 순서가 보장됨
        Map<String, String> map = new LinkedHashMap<>();
        NodeList entries = mapElem.getElementsByTagName("entry");
        for (int k = 0; k < entries.getLength(); k++) {
            Element entry = (Element) entries.item(k);
            map.put(entry.getAttribute("key"), entry.getAttribute("value"));
        }
        return map;
    }

    // <list> 파싱 유틸
    private List<Object> parseList(Element listElem) throws Exception {
        List<Object> items = new ArrayList<>();
        NodeList children = listElem.getChildNodes();

        for (int k = 0; k < children.getLength(); k++) {
            if (!(children.item(k) instanceof Element)) continue;
            Element item = (Element) children.item(k);

            if ("bean".equals(item.getTagName())) {
                String clazzName = item.getAttribute("class");
                Class<?> clazz = Class.forName(clazzName);
                Object obj = clazz.getDeclaredConstructor().newInstance();
                items.add(obj);
            } else if ("ref".equals(item.getTagName())) {
                items.add(item.getAttribute("bean")); // bean ID
            }
        }

        return items;
    }
}
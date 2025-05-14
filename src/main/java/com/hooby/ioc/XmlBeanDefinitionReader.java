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
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            if (is == null) throw new FileNotFoundException("Classpath resource not found: " + path);

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

            NodeList importTags = doc.getElementsByTagName("import");
            for (int i = 0; i < importTags.getLength(); i++) {
                Element importElem = (Element) importTags.item(i);
                String resource = importElem.getAttribute("resource");
                if (!resource.isEmpty()) {
                    loadBeanDefinitions(resource);
                }
            }

            NodeList beans = doc.getElementsByTagName("bean");

            for (int i = 0; i < beans.getLength(); i++) {
                Element beanElem = (Element) beans.item(i);
                String id = beanElem.getAttribute("id");
                String className = beanElem.getAttribute("class");
                String initMethod = beanElem.getAttribute("init-method");
                String destroyMethod = beanElem.getAttribute("destroy-method");

                BeanDefinition def = new BeanDefinition(
                        id,
                        className,
                        initMethod.isEmpty() ? null : initMethod,
                        destroyMethod.isEmpty() ? null : destroyMethod
                );

                // constructor-arg 처리
                NodeList ctorArgs = beanElem.getElementsByTagName("constructor-arg");
                for (int j = 0; j < ctorArgs.getLength(); j++) {
                    Element arg = (Element) ctorArgs.item(j);

                    // <map>
                    NodeList mapTags = arg.getElementsByTagName("map");
                    if (mapTags.getLength() > 0) {
                        Element mapElem = (Element) mapTags.item(0);
                        Map<String, String> map = new LinkedHashMap<>();
                        NodeList entries = mapElem.getElementsByTagName("entry");
                        for (int k = 0; k < entries.getLength(); k++) {
                            Element entry = (Element) entries.item(k);
                            map.put(entry.getAttribute("key"), entry.getAttribute("value"));
                        }
                        def.addConstructorArg(map);
                        continue;
                    }

                    // <list>
                    NodeList listTags = arg.getElementsByTagName("list");
                    if (listTags.getLength() > 0) {
                        Element listElem = (Element) listTags.item(0);
                        NodeList children = listElem.getChildNodes();

                        List<Object> items = new ArrayList<>();
                        for (int k = 0; k < children.getLength(); k++) {
                            if (!(children.item(k) instanceof Element)) continue;
                            Element item = (Element) children.item(k);
                            if ("bean".equals(item.getTagName())) {
                                String clsName = item.getAttribute("class");
                                Class<?> cls = Class.forName(clsName);
                                Object obj = cls.getDeclaredConstructor().newInstance();
                                items.add(obj);
                            } else if ("ref".equals(item.getTagName())) {
                                items.add(item.getAttribute("bean")); // bean ID
                            }
                        }
                        def.addConstructorArg(items);
                        continue;
                    }

                    // ref 속성
                    if (arg.hasAttribute("ref")) {
                        def.addConstructorArg(arg.getAttribute("ref"));
                    }
                }

                // property 처리
                NodeList props = beanElem.getElementsByTagName("property");
                for (int j = 0; j < props.getLength(); j++) {
                    Element prop = (Element) props.item(j);
                    String name = prop.getAttribute("name");

                    if (prop.hasAttribute("ref")) {
                        def.addProperty(new PropertyValue(name, prop.getAttribute("ref")));
                        continue;
                    }

                    NodeList children = prop.getChildNodes();
                    for (int k = 0; k < children.getLength(); k++) {
                        if (!(children.item(k) instanceof Element)) continue;
                        Element child = (Element) children.item(k);

                        switch (child.getTagName()) {
                            case "map" -> {
                                Map<String, String> map = new LinkedHashMap<>();
                                NodeList entries = child.getElementsByTagName("entry");
                                for (int m = 0; m < entries.getLength(); m++) {
                                    Element entry = (Element) entries.item(m);
                                    map.put(entry.getAttribute("key"), entry.getAttribute("value"));
                                }
                                def.addProperty(new PropertyValue(name, map));
                            }
                            case "list" -> {
                                List<Object> list = new ArrayList<>();
                                NodeList items = child.getChildNodes();
                                for (int m = 0; m < items.getLength(); m++) {
                                    if (!(items.item(m) instanceof Element)) continue;
                                    Element item = (Element) items.item(m);
                                    if ("bean".equals(item.getTagName())) {
                                        String clsName = item.getAttribute("class");
                                        Class<?> cls = Class.forName(clsName);
                                        Object beanObj = cls.getDeclaredConstructor().newInstance();
                                        list.add(beanObj);
                                    } else if ("ref".equals(item.getTagName())) {
                                        list.add(item.getAttribute("bean"));
                                    }
                                }
                                def.addProperty(new PropertyValue(name, list));
                            }
                        }
                    }
                }

                factory.registerBeanDefinition(def);
            }

        } catch (Exception e) {
            throw new RuntimeException("XML 파싱 실패", e);
        }
    }
}
package com.hooby.ioc.util;

import com.hooby.ioc.BeanDefinition;
import com.hooby.ioc.PropertyValue;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BeanDefinitionParser {

    public static BeanDefinition parse(Element beanElem) {
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

        parseConstructorArgs(beanElem, def);
        parseProperties(beanElem, def);

        return def;
    }

    private static void parseConstructorArgs(Element beanElem, BeanDefinition def) {
        NodeList ctorArgs = beanElem.getElementsByTagName("constructor-arg");
        for (int j = 0; j < ctorArgs.getLength(); j++) {
            try {
                Element arg = (Element) ctorArgs.item(j);

                if (arg.getElementsByTagName("map").getLength() > 0) {
                    def.addConstructorArg(ElementValueParser.parseMap((Element) arg.getElementsByTagName("map").item(0)));
                    continue;
                }

                if (arg.getElementsByTagName("list").getLength() > 0) {
                    def.addConstructorArg(ElementValueParser.parseList((Element) arg.getElementsByTagName("list").item(0)));
                    continue;
                }

                if (arg.hasAttribute("ref")) {
                    def.addConstructorArg(arg.getAttribute("ref"));
                }

            } catch (Exception e) {
                System.err.println("⚠️ constructor-arg 파싱 실패: " + e.getMessage());
            }
        }
    }

    private static void parseProperties(Element beanElem, BeanDefinition def) {
        NodeList props = beanElem.getElementsByTagName("property");
        for (int j = 0; j < props.getLength(); j++) {
            try {
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
                        case "map" -> def.addProperty(new PropertyValue(name, ElementValueParser.parseMap(child)));
                        case "list" -> def.addProperty(new PropertyValue(name, ElementValueParser.parseList(child)));
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ property 파싱 실패: " + e.getMessage());
            }
        }
    }
}
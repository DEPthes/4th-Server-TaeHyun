package com.hooby.ioc.util;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;

public class ElementValueParser {

    public static Map<String, String> parseMap(Element mapElem) {
        Map<String, String> map = new LinkedHashMap<>();
        NodeList entries = mapElem.getElementsByTagName("entry");
        for (int i = 0; i < entries.getLength(); i++) {
            try {
                Element entry = (Element) entries.item(i);
                map.put(entry.getAttribute("key"), entry.getAttribute("value"));
            } catch (Exception e) {
                System.err.println("⚠️ map entry 파싱 실패: " + e.getMessage());
            }
        }
        return map;
    }

    public static List<Object> parseList(Element listElem) {
        List<Object> items = new ArrayList<>();
        NodeList children = listElem.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            if (!(children.item(i) instanceof Element)) continue;
            Element item = (Element) children.item(i);

            try {
                if ("bean".equals(item.getTagName())) {
                    String clazzName = item.getAttribute("class");
                    try {
                        Class<?> clazz = Class.forName(clazzName);
                        Object obj = clazz.getDeclaredConstructor().newInstance();
                        items.add(obj);
                    } catch (Exception e) {
                        System.err.println("❌ 리스트 bean 생성 실패: " + clazzName + " → " + e.getMessage());
                    }
                } else if ("ref".equals(item.getTagName())) {
                    items.add(item.getAttribute("bean"));
                }
            } catch (Exception e) {
                System.err.println("⚠️ 리스트 항목 파싱 실패: " + e.getMessage());
            }
        }

        return items;
    }
}
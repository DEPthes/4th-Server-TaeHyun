package com.hooby.ioc;

import com.hooby.ioc.util.BeanDefinitionParser;
import com.hooby.ioc.util.XmlDocumentLoader;
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
            Document doc = XmlDocumentLoader.loadDocument(path);

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
                BeanDefinition def = BeanDefinitionParser.parse(beanElem);
                factory.registerBeanDefinition(def);
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ XML 파싱 실패: " + path, e);
        }
    }
}
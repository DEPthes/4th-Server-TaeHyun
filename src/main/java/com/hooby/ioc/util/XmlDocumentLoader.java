package com.hooby.ioc.util;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class XmlDocumentLoader {

    public static Document loadDocument(String path) {
        try {
            InputStream is = XmlDocumentLoader.class.getClassLoader().getResourceAsStream(path);
            if (is == null) throw new FileNotFoundException("Classpath resource not found: " + path);
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        } catch (Exception e) {
            throw new RuntimeException("❌ XML 문서 로딩 실패: " + path, e);
        }
    }
}
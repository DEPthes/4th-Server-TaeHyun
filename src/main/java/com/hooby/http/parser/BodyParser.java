package com.hooby.http.parser;

import java.io.BufferedReader;
import java.util.Map;

public class BodyParser {
    public static String parse(BufferedReader reader, Map<String, String> headers, String method) throws Exception {
        if (!(method.equals("POST") || method.equals("PUT") || method.equals("PATCH"))) {
            return "";
        }

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        if (contentLength == 0) return "";

        char[] buf = new char[contentLength];
        reader.read(buf, 0, contentLength);
        return new String(buf);
    }
}
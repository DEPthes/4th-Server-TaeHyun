package com.hooby.http.parser;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class HeaderParser {
    public static Map<String, String> parse(BufferedReader reader) throws Exception {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] parts = line.split(": ", 2);
            if (parts.length == 2) {
                headers.put(parts[0], parts[1]);
            }
        }
        return headers;
    }
}
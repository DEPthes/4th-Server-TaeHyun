package com.hooby.param;

import java.util.Collections;
import java.util.Map;

public class PathParams {
    private final Map<String, String> paramMap;

    public PathParams(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public String get(String key) {
        return paramMap.get(key);
    }

    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(paramMap);
    }
}
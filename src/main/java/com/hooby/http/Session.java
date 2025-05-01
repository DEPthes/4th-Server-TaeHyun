package com.hooby.http;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, Object> getAllAttributes() {
        return Map.copyOf(attributes);
    }
}
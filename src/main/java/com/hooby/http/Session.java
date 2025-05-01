package com.hooby.http;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();

    private final long creationTime;
    private long lastAccessedTime;
    private int maxInactiveInterval = 30 * 60; // 기본 30분

    public Session(String id) {
        this.id = id;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = creationTime;
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

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void updateLastAccessedTime() {
        this.lastAccessedTime = System.currentTimeMillis();
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(int seconds) {
        this.maxInactiveInterval = seconds;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - lastAccessedTime > maxInactiveInterval * 1000L;
    }
}
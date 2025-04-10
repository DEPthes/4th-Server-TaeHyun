package com.hooby.param;

import java.util.*;

public class QueryParams {
    private final Map<String, String> queryMap = new HashMap<>();

    public QueryParams(String queryString) {
        if (queryString == null || queryString.isEmpty()) return;

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                queryMap.put(kv[0], kv[1]);
            }
        }
    }

    public String get(String key) {
        return queryMap.get(key);
    }

    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(queryMap);
    }
}
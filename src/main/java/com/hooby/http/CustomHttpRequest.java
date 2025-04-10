package com.hooby.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hooby.param.PathParams;
import com.hooby.param.QueryParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomHttpRequest {
    private String method;
    private String path;
    private String httpVersion;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    private QueryParams queryParams;
    private PathParams pathParams;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setHeaders(Map<String, String> map) {
        headers.clear();
        headers.putAll(map);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setQueryParams(QueryParams queryParams) {
        this.queryParams = queryParams;
    }

    public void setPathParams(Map<String, String> params) {
        this.pathParams = new PathParams(params);
    }

    public Map<String, Object> getJsonBody() {
        try {
            String contentType = getHeader("Content-Type");
            if (contentType == null || !contentType.startsWith("application/json")) {
                return Collections.emptyMap();
            }
            return objectMapper.readValue(getBody(), new TypeReference<>() {});
        } catch (Exception e) {
            // JSON 파싱 오류가 있어도 서버 죽이지 않기
            return Collections.emptyMap();
        }
    }

    public String getMethod() {return method;}
    public String getPath() {return path;}
    public String getHttpVersion() {return httpVersion;}
    public String getHeader(String key) {return headers.get(key);}
    public String getBody() {return body;}

    public QueryParams getQueryParams() { return queryParams; }
    public PathParams getPathParams() { return pathParams; }
}

/* 💡Descriptions
*
*   char[] buf 에서, byte stream data 인데, 왜 byte[] 가 아닌거지?
*   => InputStreamReader -> BufferedReader 로 문자단위로 처리하기 때문이다.
*
* */
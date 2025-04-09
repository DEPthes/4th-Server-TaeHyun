package com.hooby.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomHttpRequest {
    private final String method;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> headers = new HashMap<>();
    private String body;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public CustomHttpRequest(Socket clientSocket) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String requestLine = reader.readLine(); // Ex. Get /path HTTP/1.1 -> Pointer 가 requestLine 다음으로 이동

        // HTTP Message 요청 라인이 비어있으면 예외 처리 해줘야 함
        if(requestLine == null || requestLine.isEmpty()){
            throw new IllegalArgumentException("🔴Empty Request Message");
        }

        String[] elems = requestLine.split(" "); // Ex. Get /path HTTP/1.1 -> 공백 기준 분리
        method = elems[0];
        path = elems[1];
        httpVersion = elems[2];

        String headerLine;
        while(!(headerLine = reader.readLine()).isEmpty()){
            String[] headerElems = headerLine.split(": ", 2); // Key: Value Pattern
            if (headerElems.length ==2){
                headers.put(headerElems[0], headerElems[1]); // Key 와 Value 가 제대로 있다면 header 목록에 업데이트한다.
            }
        }

        // 요청의 Method가  만약 객체 바디를 필요로 하는 HTTP Method 라면?
        if("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            char[] buf = new char[contentLength]; // Byte Stream Data 라 Char Array 로 설정한다. (TCP를 알면 당연한 말)
            reader.read(buf, 0, contentLength); // buffer 에서 0번 offset 기점으로 contentLength 만큼 읽겠다.
            body = new String(buf);
        }
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
}

/* 💡Descriptions
*
*   char[] buf 에서, byte stream data 인데, 왜 byte[] 가 아닌거지?
*   => InputStreamReader -> BufferedReader 로 문자단위로 처리하기 때문이다.
*
* */
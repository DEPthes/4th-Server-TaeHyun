package com.hooby.parser;

import com.hooby.http.CustomHttpRequest;
import com.hooby.param.QueryParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    public static CustomHttpRequest parse(Socket clientSocket) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String requestLine = reader.readLine(); // Ex. Get /path HTTP/1.1 -> Pointer 가 requestLine 다음으로 이동

        // HTTP Message 요청 라인이 비어있으면 예외 처리 해줘야 함
        if(requestLine == null || requestLine.isEmpty()){
            throw new IllegalArgumentException("🔴Empty Request Message");
        }

        String[] elems = requestLine.split(" "); // Ex. Get /path HTTP/1.1 -> 공백 기준 분리
        String method = elems[0];

        String[] pathAndQuery = elems[1].split("\\?", 2);
        String path = pathAndQuery[0];
        QueryParams queryParams = new QueryParams(pathAndQuery.length > 1 ? pathAndQuery[1] : null);

        String httpVersion = elems[2];

        CustomHttpRequest request = new CustomHttpRequest();
        request.setMethod(method);
        request.setPath(path);
        request.setHttpVersion(httpVersion);
        request.setQueryParams(new QueryParams(pathAndQuery.length > 1 ? pathAndQuery[1] : null));

        // Header 파싱
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while(!(headerLine = reader.readLine()).isEmpty()){
            String[] headerElems = headerLine.split(": ", 2); // Key: Value Pattern
            if (headerElems.length ==2){
                headers.put(headerElems[0], headerElems[1]); // Key 와 Value 가 제대로 있다면 header 목록에 업데이트한다.
            }
        }
        request.setHeaders(headers);

        // 요청의 Method가  만약 객체 바디를 필요로 하는 HTTP Method 라면?
        if("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            char[] buf = new char[contentLength]; // Byte Stream Data 라 Char Array 로 설정한다. (TCP를 알면 당연한 말)
            reader.read(buf, 0, contentLength); // buffer 에서 0번 offset 기점으로 contentLength 만큼 읽겠다.
            request.setBody(new String(buf));
        }
        return request;
    }
}

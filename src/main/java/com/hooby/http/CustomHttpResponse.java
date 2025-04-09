package com.hooby.http;

import java.util.HashMap;
import java.util.Map;

public class CustomHttpResponse {
    private int statusCode = HttpStatus.OK; // Default 는 요청이 잘 처리된거라고 가정하자
    private final Map<String, String> headers = new HashMap<>();
    private String body = "";

    public void setStatus(int statusCode){
        this.statusCode = statusCode;
    }

    public void setHeader(String key, String value){
        headers.put(key, value);
    }

    public void setBody (String body){
        this.body = body;
        // Byte Stream 으로 데이터 보낼거라 Byte 배열 길이로 구함
        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        // Map Method 를 써서, Content-Type 이 없으면 기본 타입으로 설정한다.
        headers.putIfAbsent("Content-Type", "text/plain; charset=UTF-8");
    }

    public String toHttpMessage(){
        StringBuilder message = new StringBuilder();
        message.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(HttpStatus.getStatusMessage(statusCode))
                .append("\r\n");
        headers.forEach((key, value) -> message.append(key).append(": ").append(value).append("\r\n"));
        message.append("\r\n").append(body);
        return message.toString();
    }
}

/* 💡 Descriptions
*
*   근데 왜 String 안쓰고 StringBuilder 를 쓸까? -> String 을 + 로 쓰면 + 할 때마다 매번 새로운 String 객체를 생성함
*   -> StringBuilder 는 한 객체 안에서 문자열을 계속 이어붙일 수 있어서 메모리 효율이 좋다.
*   -> String.format() 을 쓰면 되지 않나 그러면? StringBuilder 가 성능이 더 좋다함.
*
* */
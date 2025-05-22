package com.hooby.http.parser;

import com.hooby.http.CustomHttpRequest;
import com.hooby.param.QueryParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public class HttpRequestParser {

    public static CustomHttpRequest parse(Socket clientSocket) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Step 1: Request Line
        RequestLineParser.RequestLine requestLine = RequestLineParser.parse(reader.readLine());

        // Step 2: Headers
        Map<String, String> headers = HeaderParser.parse(reader);

        // Step 3: Body (if applicable)
        String body = BodyParser.parse(reader, headers, requestLine.method());

        // Construct HttpRequest
        CustomHttpRequest request = new CustomHttpRequest();
        request.setMethod(requestLine.method());
        request.setPath(requestLine.path());
        request.setHttpVersion(requestLine.httpVersion());
        request.setQueryParams(new QueryParams(requestLine.query()));
        request.setHeaders(headers);
        request.setBody(body);

        return request;
    }
}
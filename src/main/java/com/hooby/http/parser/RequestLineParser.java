package com.hooby.http.parser;

public class RequestLineParser {

    public static RequestLine parse(String line) {
        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException("🔴 Empty Request Line");
        }

        String[] parts = line.split(" ", 3);
        if (parts.length < 3) throw new IllegalArgumentException("Invalid Request Line");

        String method = parts[0];
        String[] pathAndQuery = parts[1].split("\\?", 2);
        String path = pathAndQuery[0];
        String query = pathAndQuery.length > 1 ? pathAndQuery[1] : null;
        String httpVersion = parts[2];

        return new RequestLine(method, path, query, httpVersion);
    }

    public record RequestLine(String method, String path, String query, String httpVersion) {}
}
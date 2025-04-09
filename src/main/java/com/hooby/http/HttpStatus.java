package com.hooby.http;

public class HttpStatus {
    // 2xx: Success
    public static final int OK = 200;
    public static final int CREATED = 201;

    // 4xx: Client Error
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int CONFLICT = 409;

    // 5xx: Server Error
    public static final int INTERNAL_SERVER_ERROR = 500;

    public static String getStatusMessage(int statusCode) {
        return switch (statusCode) {
            case OK -> "OK";
            case CREATED -> "Created";
            case BAD_REQUEST -> "Bad Request";
            case UNAUTHORIZED -> "Unauthorized";
            case FORBIDDEN -> "Forbidden";
            case NOT_FOUND -> "Not Found";
            case METHOD_NOT_ALLOWED -> "Method Not Allowed";
            case CONFLICT -> "Conflict";
            case INTERNAL_SERVER_ERROR -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}

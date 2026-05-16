package com.telcoapihub.common;

import org.springframework.http.HttpStatus;

/**
 * Thrown anywhere in the request path to produce a CAMARA-shaped error.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", message);
    }

    public static ApiException unauthenticated(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", message);
    }

    public static ApiException permissionDenied(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, "PERMISSION_DENIED", message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }

    public static ApiException unavailable(String message) {
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "UNAVAILABLE", message);
    }
}

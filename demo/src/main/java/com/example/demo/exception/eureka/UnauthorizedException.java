package com.example.demo.exception.eureka;

import com.example.demo.util.ErrorType;

public class UnauthorizedException extends EurekaException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message, Class<?> clazz) {
        super(ErrorType.UNAUTHORIZED, message, clazz);
    }

    public UnauthorizedException(String message, Throwable cause, Class<?> clazz) {
        super(ErrorType.UNAUTHORIZED, ErrorType.UNAUTHORIZED.getCode(), message, cause, clazz);
    }
}

package com.example.demo.config.api.response.exception;

import com.example.demo.config.api.response.ErrorType;

public class UnauthorizedException extends EscolaException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message, Class<?> clazz) {
        super(ErrorType.UNAUTHORIZED, message, clazz);
    }

    public UnauthorizedException(String message, Throwable cause, Class<?> clazz) {
        super(ErrorType.UNAUTHORIZED, ErrorType.UNAUTHORIZED.getCode(), message, cause, clazz);
    }
}

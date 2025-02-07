package com.example.demo.config.api.exception;

import com.example.demo.config.api.ErrorType;

public class UnauthorizedException extends EscolaException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(ErrorType.UNAUTHORIZED, message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(ErrorType.UNAUTHORIZED, ErrorType.UNAUTHORIZED.getCode(), message, cause);
    }
}

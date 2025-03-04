package com.example.demo.exception.escola;

import com.example.demo.util.ErrorType;

public class ForbiddenException extends EscolaException {
    private static final long serialVersionUID = 1L;

    public ForbiddenException(String message, Class<?> clazz) {
        super(ErrorType.FORBIDDEN, message, clazz);
    }

    public ForbiddenException(String message, Throwable cause, Class<?> clazz) {
        super(ErrorType.FORBIDDEN, ErrorType.FORBIDDEN.getCode(), message, cause, clazz);
    }
}

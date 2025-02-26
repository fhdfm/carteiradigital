package com.example.demo.exception.escola;

import com.example.demo.util.ErrorType;

public class ConflictException extends EscolaException {
    private static final long serialVersionUID = 1L;

    public ConflictException(String message, Class<?> clazz) {
        super(ErrorType.CONFLICT, message, clazz);
    }

    public ConflictException(String message, Throwable cause, Class<?> clazz) {
        super(ErrorType.CONFLICT, ErrorType.CONFLICT.getCode(), message, cause, clazz);
    }
}

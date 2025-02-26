package com.example.demo.config.api.response.exception;

import com.example.demo.config.api.response.ErrorType;

public class ValidationException extends EscolaException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message, Class<?> clazz) {
        super(ErrorType.VALIDATION, message, clazz);
    }

    public ValidationException(String message, Throwable cause, Class<?> clazz) {
        super(ErrorType.VALIDATION, ErrorType.VALIDATION.getCode(), message, cause, clazz);
    }
}


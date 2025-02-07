package com.example.demo.config.api.exception;

import com.example.demo.config.api.ErrorType;

public class ValidationException extends EscolaException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(ErrorType.VALIDATION, message);
    }

    public ValidationException(String message, Throwable cause) {
        super(ErrorType.VALIDATION, ErrorType.VALIDATION.getCode(), message, cause);
    }
}


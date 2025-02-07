package com.example.demo.config.api.response.exception;

import com.example.demo.config.api.response.ErrorType;

public class ConflictException extends EscolaException {
    private static final long serialVersionUID = 1L;

    public ConflictException(String message) {
        super(ErrorType.CONFLICT, message);
    }

    public ConflictException(String message, Throwable cause) {
        super(ErrorType.CONFLICT, ErrorType.CONFLICT.getCode(), message, cause);
    }
}

package com.example.demo.config.api.exception;

import com.example.demo.config.api.ErrorType;

public class NotFoundException extends EscolaException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(ErrorType.NOT_FOUND, message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(ErrorType.NOT_FOUND, ErrorType.NOT_FOUND.getCode(), message, cause);
    }
}

package com.example.demo.config.api.response.exception;

import com.example.demo.config.api.response.ErrorType;

public class EscolaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected final ErrorType errorType;
    protected final short errorCode;

    /**
     * Construtor principal.
     */
    public EscolaException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorType.getCode();
    }

    /**
     * Construtor para um errorCode
     */
    public EscolaException(ErrorType errorType, short errorCode, String message) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    /**
     * Construtor para incluir causa (Throwable).
     */
    public EscolaException(ErrorType errorType, short errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public static EscolaException ofValidation(String message) {
        return new ValidationException(message);
    }

    public static EscolaException ofUnauthorized(String message) {
        return new UnauthorizedException(message);
    }

    public static EscolaException ofNotFound(String message) {
        return new NotFoundException(message);
    }

    public static EscolaException ofConflict(String message) {
        return new ConflictException(message);
    }

    public static EscolaException ofException(String message) {
        return new EscolaException(ErrorType.EXCEPTION, message);
    }
}

package com.example.demo.config.api.response.exception;

import com.example.demo.config.api.response.ErrorType;

public class EscolaException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    protected final ErrorType errorType;
    protected final short errorCode;
    protected final Class<?> clazz;

    /**
     * Construtor principal.
     */
    public EscolaException(ErrorType errorType, String message, Class<?> clazz) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorType.getCode();
        this.clazz = clazz;
    }

    /**
     * Construtor para um errorCode
     */
    public EscolaException(ErrorType errorType, short errorCode, String message, Class<?> clazz) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.clazz = clazz;
    }

    /**
     * Construtor para incluir causa (Throwable).
     */
    public EscolaException(ErrorType errorType, short errorCode, String message, Throwable cause, Class<?> clazz) {
        super(message, cause);
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.clazz = clazz;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * Método auxiliar para capturar a classe chamadora via stack trace.
     */
    private static Class<?> getCallerClass() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        try {
            return Class.forName(stack[3].getClassName());
        } catch (ClassNotFoundException e) {
            return EscolaException.class;
        }
    }

    public static EscolaException ofNoContent(String message) {
        return new NoContentException(message, getCallerClass());
    }

    public static EscolaException ofValidation(String message) {
        return new ValidationException(message, getCallerClass());
    }

    public static EscolaException ofUnauthorized(String message) {
        return new UnauthorizedException(message, getCallerClass());
    }

    public static EscolaException ofNotFound(String message) {
        return new NotFoundException(message, getCallerClass());
    }

    public static EscolaException ofConflict(String message) {
        return new ConflictException(message, getCallerClass());
    }

    public static EscolaException ofException(String message) {
        return new EscolaException(ErrorType.EXCEPTION, message, getCallerClass());
    }
}

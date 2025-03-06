package com.example.demo.exception.eureka;

import com.example.demo.util.ErrorType;

public class EurekaException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    protected final ErrorType errorType;
    protected final short errorCode;
    protected final Class<?> clazz;

    /**
     * Construtor principal.
     */
    public EurekaException(ErrorType errorType, String message, Class<?> clazz) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorType.getCode();
        this.clazz = clazz;
    }

    /**
     * Construtor para um errorCode
     */
    public EurekaException(ErrorType errorType, short errorCode, String message, Class<?> clazz) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.clazz = clazz;
    }

    /**
     * Construtor para incluir causa (Throwable).
     */
    public EurekaException(ErrorType errorType, short errorCode, String message, Throwable cause, Class<?> clazz) {
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
            return EurekaException.class;
        }
    }

    public static EurekaException ofNoContent(String message) {
        return new NoContentException(message, getCallerClass());
    }

    public static EurekaException ofValidation(String message) {
        return new ValidationException(message, getCallerClass());
    }

    public static EurekaException ofUnauthorized(String message) {
        return new UnauthorizedException(message, getCallerClass());
    }

    public static EurekaException ofForbidden(String message) {
        return new ForbiddenException(message, getCallerClass());
    }

    public static EurekaException ofNotFound(String message) {
        return new NotFoundException(message, getCallerClass());
    }

    public static EurekaException ofConflict(String message) {
        return new ConflictException(message, getCallerClass());
    }

    public static EurekaException ofException(String message) {
        return new EurekaException(ErrorType.EXCEPTION, message, getCallerClass());
    }
}

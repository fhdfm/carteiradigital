package com.example.demo.config.api;

import com.example.demo.config.api.exception.EscolaException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse<T> {
    private boolean success;
    private ErrorType errorType;
    private short errorCode;
    private String error;
    private String internalException;

    @JsonProperty("return")
    private T result;

    ApiResponse(T result) {
        this.success = true;
        this.result = result;
    }

    ApiResponse(ErrorType errorType, short errorCode, String errorMessage) {
        this.success = false;
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.error = errorMessage;
    }

    ApiResponse(ErrorType errorType, short errorCode, String errorMessage, Throwable internalException) {
        this(errorType, errorCode, errorMessage);
        if (internalException != null) {
            this.internalException = internalException.getMessage();
        }
    }

    public static <T> ApiResponse<T> of(T t) {
        return new ApiResponse<>(t);
    }

    public static ApiResponse<String> of(ErrorType errorType, short errorCode, String errorMessage, Throwable internalException) {
        return new ApiResponse<>(errorType, errorCode, errorMessage, internalException);
    }

    public static ApiResponse<String> ofException(Exception exception) {
        return of(ErrorType.EXCEPTION, ErrorType.EXCEPTION.getCode(), exception.getMessage(), exception.getCause());
    }

    public static ApiResponse<String> ofEscolaException(EscolaException ex) {
        return of(ex.getErrorType(), ex.getErrorCode(), ex.getMessage(), ex);
    }

    public boolean isSuccess() {
        return success;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public String getError() {
        return error;
    }

    public String getInternalException() {
        return internalException;
    }

    public T getResult() {
        return result;
    }
}

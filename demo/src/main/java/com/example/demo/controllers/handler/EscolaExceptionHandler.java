package com.example.demo.controllers.handler;

import com.example.demo.config.api.ApiResponse;
import com.example.demo.config.api.exception.EscolaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EscolaExceptionHandler {

    @ExceptionHandler(EscolaException.class)
    public ResponseEntity<ApiResponse<?>> handleEscolaException(EscolaException ex) {
        ApiResponse<?> apiResponse = ApiResponse.ofEscolaException(ex);
        HttpStatus status = HttpStatus.valueOf(ex.getErrorCode());
        return new ResponseEntity<>(apiResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex) {
        ApiResponse<?> apiResponse = ApiResponse.ofException(ex);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


package com.example.demo.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.exception.eureka.NoContentException;
import com.example.demo.util.ApiReturn;
import com.example.demo.util.LogUtil;


@RestControllerAdvice
public class EurekaExceptionHandler {

    @ExceptionHandler(EurekaException.class)
    public ResponseEntity<ApiReturn<?>> handleEurekaException(EurekaException ex) {
        ApiReturn<?> apiReturn = ApiReturn.ofEurekaException(ex);
        HttpStatus status = HttpStatus.valueOf(ex.getErrorCode());

        LogUtil.log(ex.getClazz(), LogUtil.LogType.ERROR, ex);

        return new ResponseEntity<>(apiReturn, status);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ApiReturn<?>> handleNoContentException(NoContentException ex) {
        ApiReturn<?> apiReturn = ApiReturn.ofNoContentException(ex);
        HttpStatus status = HttpStatus.valueOf(ex.getErrorCode());

        LogUtil.log(ex.getClazz(), LogUtil.LogType.INFO, ex);

        return new ResponseEntity<>(apiReturn, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiReturn<?>> handleValidationException(MethodArgumentNotValidException ex) {
        return handleEurekaException(EurekaException.ofValidation(ex.getBindingResult().getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiReturn<?>> handleGeneralException(Exception ex) {
        ApiReturn<?> apiReturn = ApiReturn.ofException(ex);
        return new ResponseEntity<>(apiReturn, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


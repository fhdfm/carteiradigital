package com.example.demo.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.exception.escola.EscolaException;
import com.example.demo.exception.escola.NoContentException;
import com.example.demo.util.ApiReturn;
import com.example.demo.util.LogUtil;


@RestControllerAdvice
public class EscolaExceptionHandler {

    @ExceptionHandler(EscolaException.class)
    public ResponseEntity<ApiReturn<?>> handleEscolaException(EscolaException ex) {
        ApiReturn<?> apiReturn = ApiReturn.ofEscolaException(ex);
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
        return handleEscolaException(EscolaException.ofValidation(ex.getBindingResult().getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiReturn<?>> handleGeneralException(Exception ex) {
        ApiReturn<?> apiReturn = ApiReturn.ofException(ex);
        return new ResponseEntity<>(apiReturn, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


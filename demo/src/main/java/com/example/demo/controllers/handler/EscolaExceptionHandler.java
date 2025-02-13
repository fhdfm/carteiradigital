package com.example.demo.controllers.handler;

import com.example.demo.config.api.response.ApiReturn;
import com.example.demo.config.api.response.exception.EscolaException;
import com.example.demo.config.api.response.exception.NoContentException;
import com.example.demo.util.LogUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


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
        return new ResponseEntity<>(apiReturn, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiReturn<?>> handleGeneralException(Exception ex) {
        ApiReturn<?> apiReturn = ApiReturn.ofException(ex);
        return new ResponseEntity<>(apiReturn, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


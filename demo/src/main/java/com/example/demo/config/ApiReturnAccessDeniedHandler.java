package com.example.demo.config;

import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.util.ApiReturn;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class ApiReturnAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public ApiReturnAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        mapper.writeValue(response.getOutputStream(), ApiReturn.ofEurekaException(EurekaException.ofForbidden("Acesso negado")));
    }
}

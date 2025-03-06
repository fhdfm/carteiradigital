package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controller.doc.EurekaApiOperation;
import com.example.demo.dto.JwtAuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.service.AuthenticationService;
import com.example.demo.util.ApiReturn;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints relacionados a autenticação do usuário")
public class AuthenticationController {
    
    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }


    @PostMapping("/signin")
    @EurekaApiOperation(
            summary = "Método para fazer signin na aplicação",
            description = "Autentica o usuário e devolve para ele um token e um refresh token"
    )
    public ResponseEntity<ApiReturn<JwtAuthenticationResponse>> signin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de signin",
                    required = true
            )
            @RequestBody @Valid LoginRequest request
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.signin(request)));
    }

    @EurekaApiOperation(
            summary = "Método para fazer atualizar o token de autenticação na aplicação",
            description = "Atualiza o token a partir do refresh token e devolve"
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiReturn<JwtAuthenticationResponse>> signin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de refresh token",
                    required = true
            )
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.refreshToken(request)));
    }
}

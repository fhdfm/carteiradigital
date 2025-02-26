package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controller.doc.EscolaApiOperation;
import com.example.demo.dto.JwtAuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.service.UsuarioService;
import com.example.demo.util.ApiReturn;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints relacionados a autenticação do usuário")
public class AuthenticationController {
    
    private final UsuarioService service;

    public AuthenticationController(UsuarioService service) {
        this.service = service;
    }

    @EscolaApiOperation(
            summary = "Método para fazer signin na aplicação",
            description = "Autentica o usuário e devolve para ele um token e um refresh token"
    )
    @PostMapping("/signin")
    public ResponseEntity<ApiReturn<JwtAuthenticationResponse>> signin(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(ApiReturn.of(service.signin(request)));
    }

    @EscolaApiOperation(
            summary = "Método para fazer atualizar o token de autenticação na aplicação",
            description = "Atualiza o token a partir do refresh token e devolve"
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiReturn<JwtAuthenticationResponse>> signin(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiReturn.of(service.refreshToken(request)));
    }

}

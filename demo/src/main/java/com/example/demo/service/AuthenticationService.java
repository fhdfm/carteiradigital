package com.example.demo.service;

import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.JwtAuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.security.JwtService;
import com.example.demo.security.UsuarioLogado;

@Service
public class AuthenticationService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService, 
        UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    public JwtAuthenticationResponse signin(LoginRequest request) {
                
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.login(), request.password()));

        UsuarioLogado usuarioLogado = (UsuarioLogado) auth.getPrincipal();
        
        String token = jwtService.generateToken(usuarioLogado);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), usuarioLogado);
        
        return new JwtAuthenticationResponse(token, refreshToken);
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest request) {
        
        String login = this.jwtService.extractUsername(request.token());
        
        Usuario user = this.usuarioService.findByEmailComEscola(login);
        UsuarioLogado usuarioLogado = user.toUsuarioLogado();
        
        if (!jwtService.isTokenValid(request.token())) {
            throw new IllegalArgumentException("Invalid token");
        }

        String token = jwtService.generateToken(usuarioLogado);
        return new JwtAuthenticationResponse(token, request.token());
    }

}

package com.example.demo.services;

import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.JwtAuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.enums.MetodoAutenticacao;
import com.example.demo.entity.enums.Perfil;
import com.example.demo.entity.enums.Status;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.services.security.JWTService;

@Service
public class UsuarioService {
    
    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository, 
        PasswordEncoder passwordEncoder, 
        JWTService jwtService, 
        AuthenticationManager authenticationManager) {
        
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;

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
        
        Usuario user = repository.findByEmailComEscola(login).orElseThrow();
        UsuarioLogado usuarioLogado = user.toUsuarioLogado();
        
        if (!jwtService.isTokenValid(request.token())) {
            throw new IllegalArgumentException("Invalid token");
        }

        String token = jwtService.generateToken(usuarioLogado);
        return new JwtAuthenticationResponse(token, request.token());
    }

    public void create(UsuarioRequest request) {

        Usuario user = new Usuario();
        user.setNome(request.nome());
        user.setEmail(request.email());
        user.setSenha(passwordEncoder.encode(request.senha()));
        user.setPerfil(Perfil.MASTER);
        user.setMetodoAutenticacao(MetodoAutenticacao.SENHA);
        user.setCpf(request.cpf());
        user.setStatus(Status.ATIVO);
        user.setTelefone(request.telefone());

        repository.save(user);
    }

}

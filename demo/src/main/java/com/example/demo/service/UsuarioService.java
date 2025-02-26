package com.example.demo.service;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.JwtAuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.TrocarSenhaRequest;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {
    
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository, 
        PasswordEncoder passwordEncoder, 
        JwtService jwtService, 
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
        user.setPrimeiroAcesso(true);

        repository.save(user);
    }

    public Usuario findByUuid(UUID uuid) {
        return this.repository.findByUuid(uuid).orElseThrow(
                () -> new EntityNotFoundException("Usuário não encontrado"));
    }

    /**
     * 
     * @param <T>
     * @param uuid
     * @param clazz
     * @return
     */
    public <T> T findByUuid(UUID uuid, Class<T> clazz) {

        Object usuario = this.repository.findByUuid(uuid).orElseThrow(
                () -> new EntityNotFoundException("Usuário não encontrado"));

        return clazz.cast(usuario);
    }

    /**
     * 
     * @param request
     */
    public void changePassword(TrocarSenhaRequest request) {
        
        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        
        if (currentUser.possuiPerfil(Perfil.ALUNO.name())) {
            Boolean alunoPodeTrocarSenha =
                this.repository.isPrimeiroAcessoResponsavel(currentUser.getUuid());
            if (alunoPodeTrocarSenha != null && !alunoPodeTrocarSenha)
                EscolaException.ofValidation("O responsável ainda não habilitou sua conta.");
        }

        if (!passwordEncoder.matches(request.senhaAntiga(), currentUser.getPassword()))
            EscolaException.ofValidation("Senha incorreta.");

        if (!request.novaSenha().equals(request.confirmarNovaSenha()))
            EscolaException.ofValidation("Senha e confirmação não conferem.");

        Usuario usuario = this.findByUuid(currentUser.getUuid());
        usuario.setPrimeiroAcesso(false);
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));

        this.repository.save(usuario);     
    }

}

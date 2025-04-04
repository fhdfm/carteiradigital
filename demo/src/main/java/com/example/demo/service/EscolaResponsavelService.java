package com.example.demo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Escola;
import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class EscolaResponsavelService {
    
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    

    public EscolaResponsavelService(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }
    
    @Transactional
    public UUID criarOuAtualizarResponsavel(Escola escola, UsuarioRequest request) {

        request = request.withEscolaId(escola.getUuid());
        Optional<Usuario> adminAtual = this.usuarioRepository.findByEscolaIdAndPerfil(escola.getUuid(), Perfil.ADMIN);

        if (adminAtual.isPresent()) {
            Usuario atual = adminAtual.get();
            
            if (atual.getCpf().equals(request.cpf().replaceAll("\\D", ""))) {
                this.usuarioService.updateUser(atual.getUuid(), request);
                return atual.getUuid();
            } else {
                atual.setStatus(Status.INATIVO);
                this.usuarioRepository.save(atual);
            }
            
        }

        return this.usuarioService.createUser(request);
    }

}

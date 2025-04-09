package com.example.demo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.dto.projection.usuario.UsuarioFull;

import jakarta.transaction.Transactional;

@Service
public class EscolaResponsavelService {
    
    private final UsuarioService usuarioService;

    public EscolaResponsavelService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @Transactional
    public UUID criarOuAtualizarResponsavel(UUID escolaId, UsuarioRequest request) {

        Optional<UsuarioFull> adminAtual = this.usuarioService.findByEscolaIdAndPerfil(escolaId, Perfil.RESPONSAVEL_CONTRATUAL);

        request = request.withEscolaIdAndPerfil(escolaId, Perfil.RESPONSAVEL_CONTRATUAL);

        if (adminAtual.isPresent()) {
            UsuarioFull atual = adminAtual.get();
            
            if (atual.getCpf().equals(request.cpf().replaceAll("\\D", ""))) {
                this.usuarioService.updateUser(atual.getUuid(), request);
                return atual.getUuid();
            } else {
                this.usuarioService.trocarResponsavelDaEscola(atual.getUuid());
            }
            
        }

        return this.usuarioService.createUser(request);
    }

    public UsuarioFull findResponsavelByEscolaId(UUID uuid) {

        UsuarioFull responsavel = this.usuarioService.findByEscolaIdAndPerfil(uuid, Perfil.RESPONSAVEL_CONTRATUAL)
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado."));

        return responsavel;

    }

}

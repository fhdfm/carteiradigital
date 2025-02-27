package com.example.demo.security.accesscontrol.policy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.model.Usuario;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.security.accesscontrol.EntityNames;
import com.example.demo.service.UsuarioService;

@Component
public class UsuarioAccessPolicy implements AccessPolicy {

    private final UsuarioService usuarioService;
    
    public UsuarioAccessPolicy(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public String getEntityName() {
        return EntityNames.USUARIO;
    }

    @Override
    public boolean hasAccess(UsuarioLogado currentUser, Object resourceId) {
        // Acesso irrestrito para MASTER
        if (currentUser.possuiPerfil(Perfil.MASTER)) {
            return true;
        }
    
        UUID targetUuid = parseResourceId(resourceId);
    
        // Buscar o usuário alvo
        Usuario userEntity = usuarioService.findByUuid(targetUuid);
        if (userEntity == null) {
            // Caso não encontre o usuário, pode ser prudente negar acesso ou lançar exceção
            return false;
        }
    
        // Armazenar os perfis em variáveis para melhor legibilidade
        boolean isAdmin = currentUser.possuiPerfil(Perfil.ADMIN);
        boolean isFuncionario = currentUser.possuiPerfil(Perfil.FUNCIONARIO);
        boolean isPdv = currentUser.possuiPerfil(Perfil.PDV);
        boolean isAluno = currentUser.possuiPerfil(Perfil.ALUNO);
        boolean isResponsavel = currentUser.possuiPerfil(Perfil.RESPONSAVEL);
    
        // Se o usuário é ADMIN ou FUNCIONARIO: acesso permitido se as escolas coincidirem
        if (isAdmin || isFuncionario) {
            return currentUser.getEscolaUuid().equals(userEntity.getEscola().getUuid());
        }
    
        // Se o usuário é PDV, ALUNO ou RESPONSAVEL: só pode acessar seus próprios dados
        if (isPdv || isAluno || isResponsavel) {
            return userEntity.getUuid().equals(currentUser.getUuid());
        }
        
        // Caso não se encaixe em nenhum perfil que permita acesso, nega acesso
        return false;
    }
    
    private UUID parseResourceId(Object resourceId) {
        try {
            return UUID.fromString(resourceId.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ResourceId inválido: " + resourceId, e);
        }
    }    
    
}

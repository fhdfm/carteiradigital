package com.example.demo.security.accesscontrol.policy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.model.Usuario;
import com.example.demo.exception.escola.EscolaException;
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
    public boolean hasAccess(UsuarioLogado currentUser, String httpMethod, Object resourceId) {
        // MASTER tem acesso irrestrito
        if (currentUser.possuiPerfil(Perfil.MASTER)) {
            return true;
        }
    
        UUID targetUuid = parseResourceId(resourceId);
        Usuario userEntity = usuarioService.findByUuid(targetUuid); // Sem carregar perfil
    
        // Se o usuário não existir, nega acesso
        if (userEntity == null) {
            throw EscolaException.ofNotFound("Usuario (" + targetUuid + " não encontrado");
        }
    
        boolean mesmaEscola = currentUser.getEscolaUuid() != null &&
                              userEntity.getEscola() != null &&
                              currentUser.getEscolaUuid().equals(userEntity.getEscola().getUuid());
    
        boolean mesmoUsuario = userEntity.getUuid().equals(currentUser.getUuid());
    
        if ("GET".equals(httpMethod)) {
            return mesmaEscola || mesmoUsuario;
        }
    
        if ("PUT".equals(httpMethod)) {
            // ADMIN e FUNCIONÁRIO podem editar usuários da mesma escola
            if ((currentUser.possuiPerfil(Perfil.ADMIN) || currentUser.possuiPerfil(Perfil.FUNCIONARIO)) && mesmaEscola) {
                return true;
            }
            // FUNCIONÁRIO e PDV podem editar apenas a si mesmos
            return mesmoUsuario;
        }
    
        if ("DELETE".equals(httpMethod)) {
            // Apenas ADMIN e FUNCIONÁRIO podem excluir (inativar) usuários da mesma escola
            return (currentUser.possuiPerfil(Perfil.ADMIN) || currentUser.possuiPerfil(Perfil.FUNCIONARIO)) && mesmaEscola;
        }
    
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

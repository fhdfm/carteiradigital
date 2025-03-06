package com.example.demo.security.accesscontrol.policy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.model.Usuario;
import com.example.demo.exception.eureka.EurekaException;
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
    public boolean hasAccess(UsuarioLogado currentUser, String httpMethod, boolean isStatusUpdate, Object resourceId) {
        UUID targetUuid = parseResourceId(resourceId);
        Usuario userEntity = usuarioService.findUserByUuid(targetUuid);

        if (userEntity == null) {
            throw EurekaException.ofNotFound("Usuário (" + targetUuid + ") não encontrado.");
        }

        // Se a entidade for um ALUNO, encerra a lógica e nega o acesso
        if (userEntity.getPerfil() == Perfil.ALUNO) {
            return false;
        }

        UUID escolaUuid = currentUser.getEscola().getUuid();

        boolean mesmaEscola = escolaUuid != null &&
                              userEntity.getEscola() != null &&
                              escolaUuid.equals(userEntity.getEscola().getUuid());

        boolean mesmoUsuario = userEntity.getUuid().equals(currentUser.getUuid());

        // Se for ativação/inativação, apenas MASTER, ADMIN e FUNCIONÁRIO podem fazer isso
        if ("PUT".equals(httpMethod) && isStatusUpdate) {
            return (currentUser.possuiPerfil(Perfil.MASTER) || currentUser.possuiPerfil(Perfil.ADMIN) || currentUser.possuiPerfil(Perfil.FUNCIONARIO)) && mesmaEscola;
        }

        // Se for uma atualização normal de dados
        if ("PUT".equals(httpMethod)) {
            // ADMIN e FUNCIONÁRIO podem editar usuários da mesma escola
            if ((currentUser.possuiPerfil(Perfil.ADMIN) || currentUser.possuiPerfil(Perfil.FUNCIONARIO)) && mesmaEscola) {
                return true;
            }

            // RESPONSÁVEL e PDV só podem editar seus próprios dados
            if ((currentUser.possuiPerfil(Perfil.RESPONSAVEL) || currentUser.possuiPerfil(Perfil.PDV)) && mesmoUsuario) {
                return true;
            }
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
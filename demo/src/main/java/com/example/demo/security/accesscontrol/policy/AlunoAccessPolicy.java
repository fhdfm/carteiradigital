package com.example.demo.security.accesscontrol.policy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.model.Aluno;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.security.accesscontrol.EntityNames;
import com.example.demo.service.UsuarioService;

@Component
public class AlunoAccessPolicy implements AccessPolicy {

    private final UsuarioService usuarioService;

    public AlunoAccessPolicy(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public String getEntityName() {
        return EntityNames.ALUNO;
    }

    @Override
    public boolean hasAccess(UsuarioLogado currentUser, String httpMethod, boolean isStatusUpdate, Object resourceId) {
        
        UUID targetUuid = parseResourceId(resourceId);
        Aluno userEntity = this.usuarioService.findStudentWithResponsavelByUuid(targetUuid);

        if (userEntity == null) {
            throw EscolaException.ofNotFound("Aluno (" + targetUuid + ") não encontrado.");
        }

        boolean mesmaEscola = currentUser.getEscolaUuid() != null &&
                              userEntity.getEscola() != null &&
                              currentUser.getEscolaUuid().equals(userEntity.getEscola().getUuid());

        boolean mesmoUsuario = userEntity.getUuid().equals(currentUser.getUuid());

        // Se for ativação/inativação, apenas MASTER, ADMIN e FUNCIONÁRIO podem fazer isso
        if ("PUT".equals(httpMethod) && isStatusUpdate) {
            return (currentUser.possuiPerfil(Perfil.MASTER) || currentUser.possuiPerfil(Perfil.ADMIN) || currentUser.possuiPerfil(Perfil.FUNCIONARIO)) && mesmaEscola;
        }

        // Se for uma atualização normal de dados
        if ("PUT".equals(httpMethod)) {
            // ADMIN e FUNCIONÁRIO podem editar alunos da mesma escola
            if ((currentUser.possuiPerfil(Perfil.ADMIN) || currentUser.possuiPerfil(Perfil.FUNCIONARIO)) && mesmaEscola) {
                return true;
            }

            // RESPONSAVEL só pode mudar se o aluno estiver sob sua responsabilidade
            if (currentUser.possuiPerfil(Perfil.RESPONSAVEL) 
                && currentUser.getUuid().equals(userEntity.getResponsavel().getUuid())) {
                return true;
            }

            // ALUNO pode editar a si mesmo.
            if (currentUser.possuiPerfil(Perfil.ALUNO) && mesmoUsuario) {
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
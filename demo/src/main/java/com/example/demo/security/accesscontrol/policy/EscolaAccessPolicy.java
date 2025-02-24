package com.example.demo.security.accesscontrol.policy;

import org.springframework.stereotype.Component;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.security.accesscontrol.EntityNames;

@Component
public class EscolaAccessPolicy implements AccessPolicy {

    @Override
    public String getEntityName() {
        return EntityNames.ESCOLA;
    }

    @Override
    public boolean hasAccess(UsuarioLogado usuarioLogado, Object resourceId) {
        
        // Validação básica de entrada.
        if (usuarioLogado == null || resourceId == null) {
            return false;
        }
        
        // Cache dos resultados dos perfis para evitar chamadas duplicadas.
        boolean isAdmin = usuarioLogado.possuiPerfil(Perfil.ADMIN.name());
        
        // Outros perfis não possuem acesso.
        return isAdmin || resourceId.equals(usuarioLogado.getEscolaId());
    }    
    
}

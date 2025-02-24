package com.example.demo.security.accesscontrol.policy;

import com.example.demo.security.UsuarioLogado;

public interface AccessPolicy {
        
    /**
     * Retorna o nome da entidade à qual essa política se aplica.
     */
    String getEntityName();

    /**
     * Verifica se o usuário tem acesso ao recurso identificado.
     *
     * @param user       o usuário que está tentando acessar o recurso
     * @param resourceId o identificador do recurso (por exemplo, um UUID)
     * @return true se o usuário tiver acesso; false caso contrário
     */
    boolean hasAccess(UsuarioLogado user, Object resourceId);
}

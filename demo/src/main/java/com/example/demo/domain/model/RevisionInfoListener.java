package com.example.demo.domain.model;

import org.hibernate.envers.RevisionListener;

import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;

// TODO: Mudar para um local apropriado
public class RevisionInfoListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        
        RevisionInfo rev = (RevisionInfo) revisionEntity;

        UsuarioLogado usuarioLogado = SecurityUtils.getUsuarioLogado();
        rev.setNome(usuarioLogado.getName());
        rev.setUsuarioId(usuarioLogado.getId());
        
        if (usuarioLogado.getEscola() != null) {
            rev.setEscolaId(usuarioLogado.getEscola().getId());
        }

    }
    
}

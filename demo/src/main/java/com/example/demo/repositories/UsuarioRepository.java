package com.example.demo.repositories;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Usuario;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
    
}

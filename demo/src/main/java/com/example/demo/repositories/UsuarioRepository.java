package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Usuario;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
 
    Optional<Usuario> findByEmail(String email);

}

package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Usuario;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
 
    /* Utilizado para o login */
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.escola WHERE u.email = :email")
    Optional<Usuario> findByEmailComEscola(@Param("email") String email);
    
    Optional<Usuario> findByEmail(String email);
}

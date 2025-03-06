package com.example.demo.service.replica;

import com.example.demo.domain.model.Escola;
import com.example.demo.dto.UsuarioView;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.EscolaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EscolaUsuarioService {

    private final EscolaRepository escolaRepository;
    private final UsuarioRepository usuarioRepository;

    public EscolaUsuarioService(EscolaRepository escolaRepository, UsuarioRepository usuarioRepository) {
        this.escolaRepository = escolaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<UsuarioView> findAllByEscolaUuid(UUID escolaId, Pageable pageable) {
        return usuarioRepository.findAllByEscolaUuid(escolaId, pageable);
    }

    public Escola findByUuid(UUID uuid) {
        return escolaRepository.findByUuid(uuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Escola não encontrada."));
    }
}

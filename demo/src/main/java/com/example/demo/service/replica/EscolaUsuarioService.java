package com.example.demo.service.replica;

import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Aluno;
import com.example.demo.domain.model.Escola;
import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.AlunoRequest;
import com.example.demo.dto.TrocarSenhaRequest;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.dto.UsuarioView;
import com.example.demo.dto.projection.aluno.AlunoSummary;
import com.example.demo.dto.projection.usuario.UsuarioSummary;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.EscolaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.specification.AlunoSpecification;
import com.example.demo.repository.specification.UsuarioSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
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
                .orElseThrow(() -> EscolaException.ofNotFound("Escola não encontrada."));
    }
}

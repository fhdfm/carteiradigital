package com.example.demo.domain.model;

import java.util.UUID;

public record VerificacaoCartaoRequest(
    UUID usuarioId,
    String senha
) {}

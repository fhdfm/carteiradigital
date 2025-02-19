package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.entity.enums.Status;

public interface EscolaView {
    UUID getUuid();
    String getNome();
    String getCnpj();
    Status getStatus();
}

package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.entity.enums.Status;

public record EscolaView(UUID uuid, String nome, String cnpj, Status status) {


}

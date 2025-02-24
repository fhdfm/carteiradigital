package com.example.demo.dto;

import com.example.demo.validation.annotation.CNPJ;

public record EscolaRequest(String nome, @CNPJ String cnpj) {
    
}

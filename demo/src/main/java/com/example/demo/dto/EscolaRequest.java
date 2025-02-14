package com.example.demo.dto;

import com.example.demo.validation.annotations.CNPJ;

public record EscolaRequest(String nome, @CNPJ String cnpj) {
    
}

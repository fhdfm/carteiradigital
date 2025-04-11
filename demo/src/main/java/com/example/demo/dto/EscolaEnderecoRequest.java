package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EscolaEnderecoRequest(

    @NotBlank(message = "Telefone é obrigatório.")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres.")
    String telefone,

    @NotBlank(message = "CEP é obrigatório.")
    @Size(max = 10, message = "CEP deve ter no máximo 10 caracteres.")
    String cep,

    @NotBlank(message = "Endereço é obrigatório.")
    @Size(max = 100, message = "Endereço deve ter no máximo 100 caracteres.")
    String endereco,

    @NotBlank(message = "Número é obrigatório.")
    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres.")
    String numero,

    @NotBlank(message = "Bairro é obrigatório.")
    @Size(max = 50, message = "Bairro deve ter no máximo 50 caracteres.")
    String bairro,

    @NotBlank(message = "Complemento é obrigatório.")
    @Size(max = 50, message = "Complemento deve ter no máximo 50 caracteres.")
    String complemento,

    @NotBlank(message = "Cidade é obrigatória.")
    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres.")
    String cidade,

    @NotBlank(message = "Estado é obrigatório.")
    @Size(max = 2, message = "Estado deve ter exatamente 2 caracteres.")
    String estado

) {}

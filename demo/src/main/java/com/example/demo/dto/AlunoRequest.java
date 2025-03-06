package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.validation.annotation.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlunoRequest(

    @NotNull(message = "Escola ID é obrigatório")
    UUID escolaId,

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres")
    String nome,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email válido")
    String email,

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    String cpf,

    @NotBlank(message = "O telefone é obrigatório")
    String telefone,

    @NotNull(message = "Responsável ID é obrigatório")
    UUID responsavelId,

    @NotBlank(message = "A matrícula é obrigatória")
    String matricula

) {}

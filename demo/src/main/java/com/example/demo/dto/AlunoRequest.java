package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.validation.annotation.CPF;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlunoRequest(
//    @Schema(description = "Identificador da escola", example = "00000000-0000-0000-0000-000000000000")
//    @NotNull(message = "Escola ID é obrigatório")
//    UUID escolaId,

    @Schema(description = "Nome completo do aluno", example = "João da Silva")
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres")
    String nome,

    @Schema(description = "Email do aluno", example = "email@email.com")
//    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email válido")
    String email,

    @Schema(description = "CPF do aluno", example = "000.000.000-00")
    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    String cpf,

    @Schema(description = "Telefone do aluno", example = "00000000000")
//    @NotBlank(message = "O telefone é obrigatório")
    String telefone,

//    @Schema(description = "Identificador do responsável pelo aluno", example = "00000000-0000-0000-0000-000000000000")
//    @NotNull(message = "Responsável ID é obrigatório")
//    UUID responsavelId,

    @Schema(description = "Número de matrícula do aluno", example = "00000000")
    @NotBlank(message = "A matrícula é obrigatória")
    String matricula,

    @Schema(description = "Número do cartão", example = "00000000000")
    @Size(min = 1, max = 30, message = "Número deve ter entre 1 e 30 caracteres")
    String numeroCartao

) {}

package com.example.demo.dto;

import com.example.demo.validation.annotation.CPF;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfessorRequest(

    @Schema(description = "Nome completo do professor", example = "Maria Oliveira")
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres")
    String nome,

    @Schema(description = "Email do professor", example = "professor@email.com")
    @Email(message = "Informe um email válido")
    String email,

    @Schema(description = "CPF do professor", example = "000.000.000-00")
    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    String cpf,

    @Schema(description = "Telefone do professor", example = "11999999999")
    String telefone,

    @Schema(description = "Número de registro funcional ou matrícula do professor", example = "PRF123456")
    @NotBlank(message = "O registro funcional é obrigatório")
    String registroFuncional,

    @Schema(description = "Número do cartão do professor (caso utilize sistema de entrada)", example = "1234567890")
    @Size(min = 1, max = 30, message = "Número do cartão deve ter entre 1 e 30 caracteres")
    String numeroCartao

) {}

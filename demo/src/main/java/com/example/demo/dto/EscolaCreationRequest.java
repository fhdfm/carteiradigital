package com.example.demo.dto;

import com.example.demo.validation.annotation.CNPJ;
import com.example.demo.validation.annotation.CPF;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EscolaCreationRequest(
        @Schema(description = "Nome da escola", example = "Nome da Escola")
        @NotBlank(message = "Nome deve ser preenchido")
        @Size(min = 5, message = "Nome deve ter pelo menos 5 caracteres")
        String nome,
        @Schema(description = "CNPJ da escola", example = "00.000.000/0001-00")
        @NotBlank(message = "CNPJ deve ser preenchido")
        @CNPJ String cnpj,

        @Schema(description = "Nome do admin", example = "Nome")
        @NotBlank(message = "O nome do admin é obrigatório")
        @Size(min = 3, max = 255, message = "O nome do admin deve ter entre 3 e 255 caracteres")
        String nomeAdmin,

        @Schema(description = "Email do admin", example = "email@email.com")
        @NotBlank(message = "O email do admin é obrigatório")
        @Email(message = "Informe um email válido")
        String emailAdmin,

        @Schema(description = "CPF do admin", example = "000.000.000-00")
        @NotBlank(message = "O CPF do admin é obrigatório")
        @CPF(message = "CPF inválido")
        String cpfAdmin,

        @Schema(description = "Telefone do admin", example = "00000000000")
        @NotBlank(message = "O telefone do admin é obrigatório")
        String telefoneAdmin
) {
    
}

package com.example.demo.dto;

import com.example.demo.domain.enums.Departamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoRequest(
        @Schema(description = "Nome do produto", example = "Notebook Gamer")
        @NotBlank(message = "Nome deve ser preenchido")
        @Size(min = 3, message = "Nome deve ter pelo menos 3 caracteres")
        String nome,

        @Schema(description = "URL da foto do produto", example = "https://exemplo.com/imagem.jpg")
        String foto,

        @Schema(description = "Preço do produto", example = "4999.99")
        @NotNull(message = "Preço deve ser informado")
        @Positive(message = "Preço deve ser um valor positivo")
        BigDecimal preco,

        @Schema(description = "UUID da categoria associada ao produto", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "Categoria deve ser informada")
        UUID categoriaId,

        @Schema(description = "Departamento do produto", example = "ELETRONICOS")
        @NotNull(message = "Departamento deve ser informado")
        Departamento departamento
) {
}

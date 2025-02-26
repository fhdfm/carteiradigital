package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TrocarSenhaRequest(
                                @Schema(description = "Senha antiga", example = "^%&^%&^%^&%")
                                @NotBlank(message = "Informe a senha antiga.") 
                                String senhaAntiga,
                                @Schema(description = "Senha", example = "m!nh@$Enha")
                                @NotBlank(message = "Senha deve ser preenchida.") 
                                String novaSenha, 
                                @Schema(description = "Confirmaçãode senha", example = "m!nh@$Enha")
                                @NotBlank(message = "Confirmação deve ser preenchida.")
                                String confirmarNovaSenha
                            ) { }

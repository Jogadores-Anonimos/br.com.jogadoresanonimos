package br.com.jogadoresanonimos.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaDesativarRequest(
        @NotBlank(message = "Senha é obrigatória") String senha
) {
}

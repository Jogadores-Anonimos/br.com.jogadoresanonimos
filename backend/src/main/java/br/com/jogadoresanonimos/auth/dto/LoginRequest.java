package br.com.jogadoresanonimos.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Informe apelido ou e-mail") String identificador,
        @NotBlank(message = "Senha é obrigatória") String senha
) {
}

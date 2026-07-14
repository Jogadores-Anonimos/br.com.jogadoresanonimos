package br.com.jogadoresanonimos.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequest(
        @NotBlank(message = "Token é obrigatório") String token,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 10, message = "Senha deve ter no mínimo 10 caracteres")
        String novaSenha,

        @NotBlank(message = "Confirmação de nova senha é obrigatória")
        String confirmarNovaSenha
) {
}

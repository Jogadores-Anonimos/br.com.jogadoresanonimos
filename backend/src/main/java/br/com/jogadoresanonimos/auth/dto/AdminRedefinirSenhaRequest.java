package br.com.jogadoresanonimos.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminRedefinirSenhaRequest(
        @NotBlank(message = "Código é obrigatório")
        @Pattern(regexp = "\\d{6}", message = "Código deve ter 6 dígitos numéricos")
        String codigo,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 10, message = "Senha deve ter no mínimo 10 caracteres")
        String novaSenha,

        @NotBlank(message = "Confirmação de nova senha é obrigatória")
        String confirmarNovaSenha
) {
}

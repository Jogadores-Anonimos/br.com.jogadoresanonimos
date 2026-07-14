package br.com.jogadoresanonimos.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequest(
        @NotBlank(message = "Senha atual é obrigatória")
        String senhaAtual,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 10, message = "Nova senha deve ter no mínimo 10 caracteres")
        String novaSenha,

        @NotBlank(message = "Confirmação de nova senha é obrigatória")
        String confirmarNovaSenha
) {
}

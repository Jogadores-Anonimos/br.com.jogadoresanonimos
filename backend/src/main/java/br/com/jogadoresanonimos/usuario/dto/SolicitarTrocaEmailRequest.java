package br.com.jogadoresanonimos.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitarTrocaEmailRequest(
        @NotBlank(message = "Senha atual é obrigatória")
        String senhaAtual,

        @NotBlank(message = "Novo e-mail é obrigatório")
        @Email(message = "Digite um e-mail válido, como nome@exemplo.com")
        String novoEmail
) {
}

package br.com.jogadoresanonimos.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ReenviarConfirmacaoEmailRequest(
        @NotBlank(message = "Informe apelido ou e-mail") String identificador
) {
}

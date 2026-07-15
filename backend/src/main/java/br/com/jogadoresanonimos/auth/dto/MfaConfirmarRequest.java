package br.com.jogadoresanonimos.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MfaConfirmarRequest(
        @NotBlank(message = "Código TOTP é obrigatório")
        @Pattern(regexp = "^[0-9]{6}$", message = "Código TOTP deve ter 6 dígitos")
        String codigoTotp
) {
}

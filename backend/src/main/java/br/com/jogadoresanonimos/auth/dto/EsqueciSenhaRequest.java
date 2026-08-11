package br.com.jogadoresanonimos.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record EsqueciSenhaRequest(
        @NotBlank(message = "Informe apelido ou e-mail") String identificador,
        @NotBlank(message = "Verificação do reCAPTCHA é obrigatória") String recaptchaToken
) {
}

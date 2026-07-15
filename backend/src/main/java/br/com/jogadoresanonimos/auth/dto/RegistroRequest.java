package br.com.jogadoresanonimos.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "Apelido é obrigatório")
        @Size(min = 3, max = 30, message = "Apelido deve ter entre 3 e 30 caracteres")
        @Pattern(regexp = "\\S+", message = "Apelido não pode conter espaços")
        String apelido,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail em formato inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 10, message = "Senha deve ter no mínimo 10 caracteres")
        String senha,

        @NotBlank(message = "Confirmação de senha é obrigatória")
        String confirmarSenha,

        @NotBlank(message = "Verificação do reCAPTCHA é obrigatória")
        String recaptchaToken
) {
}

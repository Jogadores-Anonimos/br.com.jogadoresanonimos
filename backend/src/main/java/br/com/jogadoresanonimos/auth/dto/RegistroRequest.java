package br.com.jogadoresanonimos.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "Escolha um apelido para continuar")
        @Size(min = 3, max = 30, message = "O apelido deve ter entre 3 e 30 caracteres")
        @Pattern(regexp = "\\S+", message = "O apelido não pode conter espaços")
        String apelido,

        @NotBlank(message = "Informe um e-mail para continuar")
        @Email(message = "Digite um e-mail válido, como nome@exemplo.com")
        String email,

        @NotBlank(message = "Crie uma senha para continuar")
        @Size(min = 10, message = "A senha deve ter no mínimo 10 caracteres")
        String senha,

        @NotBlank(message = "Confirme a senha para continuar")
        String confirmarSenha,

        @NotBlank(message = "Marque a verificação \"Não sou um robô\" para continuar")
        String recaptchaToken
) {
}

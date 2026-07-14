package br.com.jogadoresanonimos.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarContaRequest(
        @NotBlank(message = "Apelido é obrigatório")
        @Size(min = 3, max = 30, message = "Apelido deve ter entre 3 e 30 caracteres")
        @Pattern(regexp = "\\S+", message = "Apelido não pode conter espaços")
        String apelido,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail em formato inválido")
        String email
) {
}

package br.com.jogadoresanonimos.vinteperguntas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VinteQuestaoUpdateRequest(
        @NotBlank(message = "Texto é obrigatório") @Size(max = 500, message = "Texto deve ter no máximo 500 caracteres") String texto
) {
}

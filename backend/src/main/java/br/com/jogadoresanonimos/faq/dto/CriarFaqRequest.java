package br.com.jogadoresanonimos.faq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarFaqRequest(
        @NotBlank @Size(max = 500) String pergunta,
        @NotBlank @Size(max = 4000) String resposta
) {
}

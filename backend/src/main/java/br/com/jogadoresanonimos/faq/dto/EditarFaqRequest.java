package br.com.jogadoresanonimos.faq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditarFaqRequest(
        @NotBlank @Size(max = 500) String pergunta,
        @NotBlank @Size(max = 4000) String resposta
) {
}

package br.com.jogadoresanonimos.reflexao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SalvarReflexaoDiaRequest(
        @NotBlank @Size(max = 2000) String texto
) {
}

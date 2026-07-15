package br.com.jogadoresanonimos.escritorio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgendaItemRequest(
        @NotBlank String diaSemana,
        @NotBlank @Size(max = 50) String horario
) {
}

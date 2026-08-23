package br.com.jogadoresanonimos.reuniao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReuniaoAgendaItemRequest(
        @NotBlank String diaSemana,
        @NotBlank @Size(max = 50) String horario
) {
}

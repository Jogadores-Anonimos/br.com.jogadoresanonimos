package br.com.jogadoresanonimos.relato.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditarRelatoRequest(
        @NotBlank @Size(min = 50, max = 10000) String texto
) {
}

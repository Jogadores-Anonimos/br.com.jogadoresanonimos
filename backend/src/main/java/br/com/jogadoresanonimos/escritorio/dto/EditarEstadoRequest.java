package br.com.jogadoresanonimos.escritorio.dto;

import jakarta.validation.constraints.NotBlank;

public record EditarEstadoRequest(
        @NotBlank String uf
) {
}

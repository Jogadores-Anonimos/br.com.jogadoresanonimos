package br.com.jogadoresanonimos.escritorio.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarEstadoRequest(
        @NotBlank String uf
) {
}

package br.com.jogadoresanonimos.escritorio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditarRegiaoRequest(
        @NotBlank @Size(max = 100) String nome
) {
}

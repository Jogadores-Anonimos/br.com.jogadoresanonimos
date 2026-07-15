package br.com.jogadoresanonimos.hotline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditarRegiaoRequest(
        @NotBlank String uf,
        @NotBlank @Size(max = 150) String cidade
) {
}

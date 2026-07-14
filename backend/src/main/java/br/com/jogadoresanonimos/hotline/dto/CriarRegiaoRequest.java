package br.com.jogadoresanonimos.hotline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarRegiaoRequest(
        @NotBlank @Size(max = 100) String nome
) {
}

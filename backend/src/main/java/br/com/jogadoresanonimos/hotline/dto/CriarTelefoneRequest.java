package br.com.jogadoresanonimos.hotline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarTelefoneRequest(
        @NotBlank @Size(max = 100) String label,
        @NotBlank @Size(max = 30) String telefone
) {
}

package br.com.jogadoresanonimos.hotline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditarTelefoneRequest(
        @NotBlank @Size(max = 30) String telefone
) {
}

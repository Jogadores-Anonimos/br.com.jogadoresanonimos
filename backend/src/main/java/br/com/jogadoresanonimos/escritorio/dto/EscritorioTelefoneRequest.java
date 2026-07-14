package br.com.jogadoresanonimos.escritorio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EscritorioTelefoneRequest(
        @Size(max = 100) String label,
        @NotBlank @Size(max = 100) String telefone
) {
}

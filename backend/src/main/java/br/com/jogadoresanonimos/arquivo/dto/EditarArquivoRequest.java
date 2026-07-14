package br.com.jogadoresanonimos.arquivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditarArquivoRequest(
        @NotBlank @Size(max = 300) String titulo,
        @Size(max = 500) String descricao
) {
}

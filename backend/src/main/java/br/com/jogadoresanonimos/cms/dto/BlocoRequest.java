package br.com.jogadoresanonimos.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BlocoRequest(
        @NotNull(message = "Ordem é obrigatória") Integer ordem,
        @NotBlank(message = "Tipo é obrigatório") String tipo,
        String conteudo,
        UUID imagemId,
        String alt,
        UUID arquivoId
) {
}

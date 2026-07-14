package br.com.jogadoresanonimos.mural.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record EditarBannerMuralRequest(
        @NotNull UUID imagemId,
        @NotBlank @Size(max = 300) String altText,
        @Size(max = 200) String legenda,
        @Size(max = 500) String linkDestino,
        boolean ativo
) {
}

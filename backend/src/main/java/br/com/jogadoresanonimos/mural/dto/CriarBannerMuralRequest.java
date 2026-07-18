package br.com.jogadoresanonimos.mural.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarBannerMuralRequest(
        UUID imagemId,
        @Size(max = 500) String videoUrl,
        @NotBlank @Size(max = 300) String altText,
        @Size(max = 200) String legenda,
        @Size(max = 500) String linkDestino
) {
}

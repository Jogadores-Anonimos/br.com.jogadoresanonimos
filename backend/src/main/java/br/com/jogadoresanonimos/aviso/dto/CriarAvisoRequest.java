package br.com.jogadoresanonimos.aviso.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CriarAvisoRequest(
        @NotBlank @Size(max = 200) String titulo,
        @NotBlank @Size(max = 2000) String corpo,
        boolean fixado,
        @Future Instant dataExpiracao
) {
}

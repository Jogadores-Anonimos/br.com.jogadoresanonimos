package br.com.jogadoresanonimos.cms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CmsPaginaUpdateRequest(
        @NotBlank(message = "Título é obrigatório") String titulo,
        @NotEmpty(message = "Página precisa de ao menos um bloco") @Valid List<BlocoRequest> blocos
) {
}

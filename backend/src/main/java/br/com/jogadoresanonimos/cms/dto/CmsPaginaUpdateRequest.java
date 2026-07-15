package br.com.jogadoresanonimos.cms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CmsPaginaUpdateRequest(
        @NotEmpty(message = "Página precisa de ao menos um bloco") @Valid List<BlocoRequest> blocos
) {
}

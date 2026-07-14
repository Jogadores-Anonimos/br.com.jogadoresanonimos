package br.com.jogadoresanonimos.cms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CmsPaginaCreateRequest(
        @NotBlank(message = "Slug é obrigatório")
        @Pattern(regexp = "[a-z0-9]+(-[a-z0-9]+)*", message = "Slug deve estar em kebab-case")
        String slug,
        @NotBlank(message = "Título é obrigatório") String titulo,
        @NotEmpty(message = "Página precisa de ao menos um bloco") @Valid List<BlocoRequest> blocos
) {
}

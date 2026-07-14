package br.com.jogadoresanonimos.cms.dto;

import java.time.Instant;
import java.util.UUID;

public record CmsPaginaListItemResponse(String slug, String titulo, Instant atualizadoEm, UUID atualizadoPor) {
}

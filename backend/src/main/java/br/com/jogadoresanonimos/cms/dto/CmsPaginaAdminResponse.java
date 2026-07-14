package br.com.jogadoresanonimos.cms.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CmsPaginaAdminResponse(String slug, String titulo, Instant atualizadoEm, UUID atualizadoPor,
                                      List<BlocoDto> blocos) {
}

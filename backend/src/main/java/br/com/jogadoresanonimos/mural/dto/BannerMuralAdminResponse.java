package br.com.jogadoresanonimos.mural.dto;

import java.time.Instant;
import java.util.UUID;

public record BannerMuralAdminResponse(UUID id, UUID imagemId, String imagemUrl, String altText, String legenda,
                                        String linkDestino, boolean ativo, Instant dataPublicacao,
                                        Instant dataAtualizacao) {
}

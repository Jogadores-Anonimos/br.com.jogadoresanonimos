package br.com.jogadoresanonimos.mural.dto;

import java.time.Instant;
import java.util.UUID;

public record BannerMuralPublicoResponse(UUID id, String imagemUrl, String altText, String legenda,
                                          String linkDestino, Instant dataPublicacao) {
}

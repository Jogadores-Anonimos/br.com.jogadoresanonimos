package br.com.jogadoresanonimos.aviso.dto;

import br.com.jogadoresanonimos.aviso.entity.Aviso;

import java.time.Instant;
import java.util.UUID;

public record AvisoAdminResponse(UUID id, String titulo, String corpo, Instant dataPublicacao,
                                  boolean fixado, Instant dataExpiracao, boolean expirado) {

    public static AvisoAdminResponse from(Aviso aviso) {
        return new AvisoAdminResponse(aviso.getId(), aviso.getTitulo(), aviso.getCorpo(),
                aviso.getCriadoEm(), aviso.isFixado(), aviso.getDataExpiracao(), aviso.isExpirado());
    }
}

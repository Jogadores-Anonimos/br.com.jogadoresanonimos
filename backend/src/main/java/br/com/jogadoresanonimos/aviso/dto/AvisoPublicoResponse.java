package br.com.jogadoresanonimos.aviso.dto;

import br.com.jogadoresanonimos.aviso.entity.Aviso;

import java.time.Instant;
import java.util.UUID;

public record AvisoPublicoResponse(UUID id, String titulo, String corpo, Instant dataPublicacao,
                                    boolean fixado) {

    public static AvisoPublicoResponse from(Aviso aviso) {
        return new AvisoPublicoResponse(aviso.getId(), aviso.getTitulo(), aviso.getCorpo(),
                aviso.getCriadoEm(), aviso.isFixado());
    }
}

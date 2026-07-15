package br.com.jogadoresanonimos.relato.dto;

import br.com.jogadoresanonimos.relato.entity.Relato;

import java.time.Instant;
import java.util.UUID;

public record RelatoPublicoResponse(UUID id, String texto, String nomeExibicao, Instant dataCriacao) {

    public static RelatoPublicoResponse from(Relato relato) {
        return new RelatoPublicoResponse(relato.getId(), relato.getTexto(), relato.getNomeExibicao(), relato.getCriadoEm());
    }
}

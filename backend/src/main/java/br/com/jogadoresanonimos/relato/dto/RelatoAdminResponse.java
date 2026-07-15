package br.com.jogadoresanonimos.relato.dto;

import br.com.jogadoresanonimos.relato.entity.Relato;

import java.time.Instant;
import java.util.UUID;

public record RelatoAdminResponse(UUID id, String texto, String nomeExibicao, String status,
                                   Instant dataCriacao, Instant dataAtualizacao) {

    public static RelatoAdminResponse from(Relato relato) {
        return new RelatoAdminResponse(relato.getId(), relato.getTexto(), relato.getNomeExibicao(),
                relato.getStatus().name(), relato.getCriadoEm(), relato.getAtualizadoEm());
    }
}

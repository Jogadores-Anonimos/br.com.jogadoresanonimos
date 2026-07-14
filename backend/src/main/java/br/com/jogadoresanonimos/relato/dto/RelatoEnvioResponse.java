package br.com.jogadoresanonimos.relato.dto;

import br.com.jogadoresanonimos.relato.entity.Relato;

import java.util.UUID;

public record RelatoEnvioResponse(UUID id, String status) {

    public static RelatoEnvioResponse from(Relato relato) {
        return new RelatoEnvioResponse(relato.getId(), relato.getStatus().name());
    }
}

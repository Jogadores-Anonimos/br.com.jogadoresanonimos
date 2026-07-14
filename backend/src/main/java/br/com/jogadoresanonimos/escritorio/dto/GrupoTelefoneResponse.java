package br.com.jogadoresanonimos.escritorio.dto;

import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupoTelefone;

import java.util.UUID;

public record GrupoTelefoneResponse(UUID id, String label, String telefone) {

    public static GrupoTelefoneResponse from(GrupoIntergrupoTelefone telefone) {
        return new GrupoTelefoneResponse(telefone.getId(), telefone.getLabel(), telefone.getTelefone());
    }
}

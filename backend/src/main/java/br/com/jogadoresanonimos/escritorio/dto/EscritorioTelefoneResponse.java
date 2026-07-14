package br.com.jogadoresanonimos.escritorio.dto;

import br.com.jogadoresanonimos.escritorio.entity.EscritorioServicoTelefone;

import java.util.UUID;

public record EscritorioTelefoneResponse(UUID id, String label, String telefone) {

    public static EscritorioTelefoneResponse from(EscritorioServicoTelefone telefone) {
        return new EscritorioTelefoneResponse(telefone.getId(), telefone.getLabel(), telefone.getTelefone());
    }
}

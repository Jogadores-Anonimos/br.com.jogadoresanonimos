package br.com.jogadoresanonimos.hotline.dto;

import br.com.jogadoresanonimos.hotline.entity.HotlineTelefone;

import java.util.UUID;

public record HotlineTelefoneResponse(UUID id, String label, String telefone) {

    public static HotlineTelefoneResponse from(HotlineTelefone telefone) {
        return new HotlineTelefoneResponse(telefone.getId(), telefone.getLabel(), telefone.getTelefone());
    }
}

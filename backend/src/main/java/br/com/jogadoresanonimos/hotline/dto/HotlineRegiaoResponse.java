package br.com.jogadoresanonimos.hotline.dto;

import br.com.jogadoresanonimos.hotline.entity.HotlineRegiao;

import java.util.List;
import java.util.UUID;

public record HotlineRegiaoResponse(UUID id, String nome, List<HotlineTelefoneResponse> telefones) {

    public static HotlineRegiaoResponse from(HotlineRegiao regiao) {
        return new HotlineRegiaoResponse(
                regiao.getId(),
                regiao.getNome(),
                regiao.getTelefones().stream().map(HotlineTelefoneResponse::from).toList()
        );
    }
}

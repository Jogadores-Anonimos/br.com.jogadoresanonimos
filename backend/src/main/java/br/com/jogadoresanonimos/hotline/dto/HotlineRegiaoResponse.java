package br.com.jogadoresanonimos.hotline.dto;

import br.com.jogadoresanonimos.hotline.entity.HotlineRegiao;

import java.util.List;
import java.util.UUID;

public record HotlineRegiaoResponse(UUID id, String uf, String cidade, List<HotlineTelefoneResponse> telefones) {

    public static HotlineRegiaoResponse from(HotlineRegiao regiao) {
        return new HotlineRegiaoResponse(
                regiao.getId(),
                regiao.getUf().name(),
                regiao.getCidade(),
                regiao.getTelefones().stream().map(HotlineTelefoneResponse::from).toList()
        );
    }
}

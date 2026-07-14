package br.com.jogadoresanonimos.cms.dto;

import java.util.UUID;

public record BlocoDto(int ordem, String tipo, String conteudo, UUID imagemId, String url, String alt,
                        UUID arquivoId, String titulo, String descricao) {

    public BlocoDto(int ordem, String tipo, String conteudo, UUID imagemId, String url, String alt) {
        this(ordem, tipo, conteudo, imagemId, url, alt, null, null, null);
    }

    public static BlocoDto arquivo(int ordem, UUID arquivoId, String url, String titulo, String descricao) {
        return new BlocoDto(ordem, "arquivo", null, null, url, null, arquivoId, titulo, descricao);
    }
}

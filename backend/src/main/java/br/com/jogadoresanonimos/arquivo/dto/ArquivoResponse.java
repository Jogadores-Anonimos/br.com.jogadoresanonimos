package br.com.jogadoresanonimos.arquivo.dto;

import br.com.jogadoresanonimos.arquivo.entity.Arquivo;

import java.time.Instant;
import java.util.UUID;

public record ArquivoResponse(UUID id, String nomeOriginal, long tamanhoBytes, String titulo, String descricao,
                               String urlDownload, String thumbnailUrl, Instant criadoEm) {

    public static ArquivoResponse from(Arquivo arquivo, String thumbnailUrl) {
        return new ArquivoResponse(arquivo.getId(), arquivo.getNomeOriginal(), arquivo.getTamanhoBytes(),
                arquivo.getTitulo(), arquivo.getDescricao(), "/api/v1/arquivos/" + arquivo.getId() + "/download",
                thumbnailUrl, arquivo.getCriadoEm());
    }
}

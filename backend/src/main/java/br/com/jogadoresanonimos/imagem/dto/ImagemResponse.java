package br.com.jogadoresanonimos.imagem.dto;

import br.com.jogadoresanonimos.imagem.entity.Imagem;

import java.time.Instant;
import java.util.UUID;

public record ImagemResponse(UUID id, String nomeOriginal, long tamanhoBytes, String alt, String url, Instant criadoEm) {

    public static ImagemResponse from(Imagem imagem, String url) {
        return new ImagemResponse(imagem.getId(), imagem.getNomeOriginal(), imagem.getTamanhoBytes(),
                imagem.getAlt(), url, imagem.getCriadoEm());
    }
}

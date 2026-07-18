package br.com.jogadoresanonimos.relato.dto;

import br.com.jogadoresanonimos.relato.entity.Relato;

import java.time.Instant;
import java.util.UUID;

public record RelatoPublicoListaResponse(UUID id, String textoResumo, String nomeExibicao, Instant dataCriacao) {

    private static final int TAMANHO_RESUMO = 300;

    public static RelatoPublicoListaResponse from(Relato relato) {
        return new RelatoPublicoListaResponse(relato.getId(), truncar(relato.getTexto()),
                relato.getNomeExibicao(), relato.getCriadoEm());
    }

    private static String truncar(String texto) {
        if (texto == null || texto.length() <= TAMANHO_RESUMO) {
            return texto;
        }
        return texto.substring(0, TAMANHO_RESUMO) + "...";
    }
}

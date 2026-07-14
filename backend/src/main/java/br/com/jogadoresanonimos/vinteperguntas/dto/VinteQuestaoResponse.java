package br.com.jogadoresanonimos.vinteperguntas.dto;

import br.com.jogadoresanonimos.vinteperguntas.entity.VinteQuestao;

import java.util.UUID;

public record VinteQuestaoResponse(UUID id, int numero, int ordem, String texto) {

    public static VinteQuestaoResponse de(VinteQuestao questao, int numero) {
        return new VinteQuestaoResponse(questao.getId(), numero, questao.getOrdem(), questao.getTexto());
    }
}

package br.com.jogadoresanonimos.faq.dto;

import br.com.jogadoresanonimos.faq.entity.Faq;

import java.util.UUID;

public record FaqResponse(UUID id, String pergunta, String resposta, int ordem) {

    public static FaqResponse from(Faq faq) {
        return new FaqResponse(faq.getId(), faq.getPergunta(), faq.getResposta(), faq.getOrdem());
    }
}

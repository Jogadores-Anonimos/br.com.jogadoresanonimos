package br.com.jogadoresanonimos.reflexao.dto;

import br.com.jogadoresanonimos.reflexao.entity.ReflexaoDia;

public record ReflexaoDiaResponse(int mes, int dia, String texto) {

    public static ReflexaoDiaResponse from(ReflexaoDia reflexaoDia) {
        return new ReflexaoDiaResponse(reflexaoDia.getMes(), reflexaoDia.getDia(), reflexaoDia.getTexto());
    }
}

package br.com.jogadoresanonimos.reuniao.dto;

import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniao;

public record ReuniaoAgendaItemResponse(String diaSemana, String horario) {
    public static ReuniaoAgendaItemResponse from(AgendaItemReuniao item) {
        return new ReuniaoAgendaItemResponse(item.getDiaSemana().name(), item.getHorario());
    }
}

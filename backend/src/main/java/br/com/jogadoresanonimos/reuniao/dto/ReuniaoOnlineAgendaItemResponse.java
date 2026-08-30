package br.com.jogadoresanonimos.reuniao.dto;

import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniaoOnline;

public record ReuniaoOnlineAgendaItemResponse(String diaSemana, String horario) {
    public static ReuniaoOnlineAgendaItemResponse from(AgendaItemReuniaoOnline item) {
        return new ReuniaoOnlineAgendaItemResponse(item.getDiaSemana().name(), item.getHorario());
    }
}

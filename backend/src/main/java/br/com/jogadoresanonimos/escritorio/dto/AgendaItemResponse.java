package br.com.jogadoresanonimos.escritorio.dto;

import br.com.jogadoresanonimos.escritorio.entity.AgendaItemGrupo;

public record AgendaItemResponse(String diaSemana, String horario) {

    public static AgendaItemResponse from(AgendaItemGrupo item) {
        return new AgendaItemResponse(item.getDiaSemana().name(), item.getHorario());
    }
}

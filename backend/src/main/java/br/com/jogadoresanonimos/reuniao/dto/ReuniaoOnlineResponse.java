package br.com.jogadoresanonimos.reuniao.dto;

import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniaoOnlineComparator;
import br.com.jogadoresanonimos.reuniao.entity.ReuniaoOnline;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReuniaoOnlineResponse(UUID id, String nome, String telefone, String descricao,
                                     List<ReuniaoOnlineAgendaItemResponse> agenda, Instant atualizadoEm) {

    public static ReuniaoOnlineResponse from(ReuniaoOnline reuniaoOnline) {
        List<ReuniaoOnlineAgendaItemResponse> agenda = reuniaoOnline.getAgenda().stream()
                .sorted(AgendaItemReuniaoOnlineComparator.porDiaEHorario())
                .map(ReuniaoOnlineAgendaItemResponse::from)
                .toList();
        return new ReuniaoOnlineResponse(reuniaoOnline.getId(), reuniaoOnline.getNome(),
                reuniaoOnline.getTelefone(), reuniaoOnline.getDescricao(), agenda, reuniaoOnline.getAtualizadoEm());
    }
}

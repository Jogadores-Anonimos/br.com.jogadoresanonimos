package br.com.jogadoresanonimos.reuniao.dto;

import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniaoComparator;
import br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.time.Instant;

public record ReuniaoResponse(UUID id, String nome, CategoriaReuniao categoria, String descricao,
                               String dia, String horario, String local, String whatsapp,
                               String diaSemana, String periodo, Double latitude, Double longitude,
                               String cep, String numero, String logradouro, String bairro,
                               String cidade, String estado, String linkReuniao,
                               List<ReuniaoAvisoResponse> avisos,
                               List<ReuniaoAgendaItemResponse> agenda,
                               UUID grupoIntergrupoId, Instant atualizadoEm) {

    public ReuniaoResponse(UUID id, String nome, CategoriaReuniao categoria, String descricao,
                            String dia, String horario, String local, String whatsapp,
                            String diaSemana, String periodo, Double latitude, Double longitude,
                            String cep, String numero, String logradouro, String bairro,
                            String cidade, String estado, String linkReuniao,
                            List<ReuniaoAvisoResponse> avisos) {
        this(id, nome, categoria, descricao, dia, horario, local, whatsapp, diaSemana, periodo,
                latitude, longitude, cep, numero, logradouro, bairro, cidade, estado, linkReuniao,
                avisos, List.of(), null, null);
    }

    public static ReuniaoResponse from(Reuniao reuniao) {
        return from(reuniao, List.of());
    }

    public static ReuniaoResponse from(Reuniao reuniao, List<ReuniaoAvisoResponse> avisos) {
        return new ReuniaoResponse(reuniao.getId(), reuniao.getNome(), reuniao.getCategoria(),
                reuniao.getDescricao(), reuniao.getDia(), reuniao.getHorario(), reuniao.getLocal(),
                reuniao.getWhatsapp(), reuniao.getDiaSemana(), reuniao.getPeriodo(),
                reuniao.getLatitude(), reuniao.getLongitude(), reuniao.getCep(), reuniao.getNumero(),
                reuniao.getLogradouro(), reuniao.getBairro(), reuniao.getCidade(), reuniao.getEstado(),
                reuniao.getLinkReuniao(), avisos, agendaDa(reuniao),
                reuniao.getGrupoIntergrupo() == null ? null : reuniao.getGrupoIntergrupo().getId(),
                reuniao.getAtualizadoEm());
    }

    private static List<ReuniaoAgendaItemResponse> agendaDa(Reuniao reuniao) {
        List<ReuniaoAgendaItemResponse> agenda = reuniao.getAgenda().stream()
                .filter(item -> item.getDiaSemana() != null)
                .sorted(AgendaItemReuniaoComparator.porDiaEHorario())
                .map(ReuniaoAgendaItemResponse::from)
                .toList();
        if (!agenda.isEmpty()) {
            return agenda;
        }
        DiaSemanaReuniao diaSemana = converterDiaSemana(reuniao.getDiaSemana(), reuniao.getDia());
        if (diaSemana == null || reuniao.getHorario() == null || reuniao.getHorario().isBlank()) {
            return List.of();
        }
        return List.of(new ReuniaoAgendaItemResponse(diaSemana.name(), reuniao.getHorario()));
    }

    private static DiaSemanaReuniao converterDiaSemana(String diaSemana, String dia) {
        String valor = diaSemana == null || diaSemana.isBlank() ? dia : diaSemana;
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String normalizado = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replace("-FEIRA", "")
                .trim();
        try {
            return DiaSemanaReuniao.valueOf(normalizado);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}

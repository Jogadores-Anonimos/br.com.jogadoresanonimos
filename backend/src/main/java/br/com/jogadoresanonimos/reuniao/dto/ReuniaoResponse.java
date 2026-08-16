package br.com.jogadoresanonimos.reuniao.dto;

import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;

import java.util.UUID;

public record ReuniaoResponse(UUID id, String nome, CategoriaReuniao categoria, String descricao,
                               String dia, String horario, String local, String whatsapp,
                               String diaSemana, String periodo, Double latitude, Double longitude,
                               String cep, String numero, String logradouro, String bairro,
                               String cidade, String estado, String linkReuniao) {

    public static ReuniaoResponse from(Reuniao reuniao) {
        return new ReuniaoResponse(reuniao.getId(), reuniao.getNome(), reuniao.getCategoria(),
                reuniao.getDescricao(), reuniao.getDia(), reuniao.getHorario(), reuniao.getLocal(),
                reuniao.getWhatsapp(), reuniao.getDiaSemana(), reuniao.getPeriodo(),
                reuniao.getLatitude(), reuniao.getLongitude(), reuniao.getCep(), reuniao.getNumero(),
                reuniao.getLogradouro(), reuniao.getBairro(), reuniao.getCidade(), reuniao.getEstado(),
                reuniao.getLinkReuniao());
    }
}

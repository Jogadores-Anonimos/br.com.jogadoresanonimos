package br.com.jogadoresanonimos.escritorio.dto;

import br.com.jogadoresanonimos.escritorio.entity.DiaSemana;
import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GrupoIntergrupoResponse(UUID id, UUID estadoId, String nome, String endereco, String bairro,
                                       String cidade, List<String> diasSemana, String horario,
                                       List<GrupoTelefoneResponse> telefones, int ordem,
                                       Instant criadoEm, Instant atualizadoEm) {

    public static GrupoIntergrupoResponse from(GrupoIntergrupo grupo) {
        return new GrupoIntergrupoResponse(grupo.getId(), grupo.getEstado().getId(), grupo.getNome(),
                grupo.getEndereco(), grupo.getBairro(), grupo.getCidade(),
                grupo.getDiasSemana().stream().map(DiaSemana::name).toList(), grupo.getHorario(),
                grupo.getTelefones().stream().map(GrupoTelefoneResponse::from).toList(), grupo.getOrdem(),
                grupo.getCriadoEm(), grupo.getAtualizadoEm());
    }
}

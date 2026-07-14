package br.com.jogadoresanonimos.escritorio.dto;

import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EstadoIntergrupoResponse(UUID id, UUID regiaoId, String uf, String nome, int ordem,
                                        List<GrupoIntergrupoResponse> grupos,
                                        Instant criadoEm, Instant atualizadoEm) {

    public static EstadoIntergrupoResponse from(EstadoIntergrupo estado) {
        return new EstadoIntergrupoResponse(estado.getId(), estado.getRegiao().getId(), estado.getUf().name(),
                estado.getUf().getNomeCompleto(), estado.getOrdem(),
                estado.getGrupos().stream().map(GrupoIntergrupoResponse::from).toList(),
                estado.getCriadoEm(), estado.getAtualizadoEm());
    }

    public static EstadoIntergrupoResponse semGrupos(EstadoIntergrupo estado) {
        return new EstadoIntergrupoResponse(estado.getId(), estado.getRegiao().getId(), estado.getUf().name(),
                estado.getUf().getNomeCompleto(), estado.getOrdem(), List.of(),
                estado.getCriadoEm(), estado.getAtualizadoEm());
    }
}

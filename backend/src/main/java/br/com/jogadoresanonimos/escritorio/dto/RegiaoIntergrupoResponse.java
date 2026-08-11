package br.com.jogadoresanonimos.escritorio.dto;

import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RegiaoIntergrupoResponse(UUID id, String nome, String codigo, int ordem,
                                        List<EstadoIntergrupoResponse> estados,
                                        Instant criadoEm, Instant atualizadoEm) {

    public static RegiaoIntergrupoResponse from(RegiaoIntergrupo regiao) {
        return new RegiaoIntergrupoResponse(regiao.getId(), regiao.getNome(), regiao.getCodigo().name(),
                regiao.getOrdem(), regiao.getEstados().stream().map(EstadoIntergrupoResponse::from).toList(),
                regiao.getCriadoEm(), regiao.getAtualizadoEm());
    }

    public static RegiaoIntergrupoResponse semEstados(RegiaoIntergrupo regiao) {
        return new RegiaoIntergrupoResponse(regiao.getId(), regiao.getNome(), regiao.getCodigo().name(),
                regiao.getOrdem(), List.of(), regiao.getCriadoEm(), regiao.getAtualizadoEm());
    }
}

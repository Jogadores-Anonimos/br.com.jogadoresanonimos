package br.com.jogadoresanonimos.escritorio.dto;

import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;

public record UnidadeFederativaResponse(String uf, String nome, String codigoRegiao) {

    public static UnidadeFederativaResponse from(UnidadeFederativa unidadeFederativa) {
        return new UnidadeFederativaResponse(unidadeFederativa.name(), unidadeFederativa.getNomeCompleto(),
                unidadeFederativa.getCodigoRegiao().name());
    }
}

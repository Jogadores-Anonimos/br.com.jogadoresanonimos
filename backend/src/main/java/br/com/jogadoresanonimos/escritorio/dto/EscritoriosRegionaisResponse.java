package br.com.jogadoresanonimos.escritorio.dto;

import java.util.List;

public record EscritoriosRegionaisResponse(List<EscritorioServicoResponse> escritorios,
                                            List<RegiaoIntergrupoResponse> regioes) {
}

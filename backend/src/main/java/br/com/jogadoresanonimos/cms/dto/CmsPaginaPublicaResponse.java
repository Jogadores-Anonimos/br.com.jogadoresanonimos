package br.com.jogadoresanonimos.cms.dto;

import java.util.List;

public record CmsPaginaPublicaResponse(String slug, String titulo, List<BlocoDto> blocos) {
}

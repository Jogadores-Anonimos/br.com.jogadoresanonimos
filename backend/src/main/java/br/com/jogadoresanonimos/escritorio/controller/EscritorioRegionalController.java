package br.com.jogadoresanonimos.escritorio.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.escritorio.dto.EscritoriosRegionaisResponse;
import br.com.jogadoresanonimos.escritorio.service.EscritorioRegionalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/escritorios-regionais")
public class EscritorioRegionalController {

    private final EscritorioRegionalService escritorioRegionalService;

    public EscritorioRegionalController(EscritorioRegionalService escritorioRegionalService) {
        this.escritorioRegionalService = escritorioRegionalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<EscritoriosRegionaisResponse>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.listarEstruturaCompleta()));
    }
}

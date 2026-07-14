package br.com.jogadoresanonimos.aviso.controller;

import br.com.jogadoresanonimos.aviso.dto.AvisoPublicoResponse;
import br.com.jogadoresanonimos.aviso.service.AvisoService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/avisos")
public class AvisoController {

    private final AvisoService avisoService;

    public AvisoController(AvisoService avisoService) {
        this.avisoService = avisoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AvisoPublicoResponse>>> listarAtivos() {
        return ResponseEntity.ok(ApiResponse.ok(avisoService.listarAtivos()));
    }
}

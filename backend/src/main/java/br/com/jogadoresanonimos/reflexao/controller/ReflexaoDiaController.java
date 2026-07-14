package br.com.jogadoresanonimos.reflexao.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaResponse;
import br.com.jogadoresanonimos.reflexao.service.ReflexaoDiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reflexao-do-dia")
public class ReflexaoDiaController {

    private final ReflexaoDiaService reflexaoDiaService;

    public ReflexaoDiaController(ReflexaoDiaService reflexaoDiaService) {
        this.reflexaoDiaService = reflexaoDiaService;
    }

    @GetMapping("/hoje")
    public ResponseEntity<ApiResponse<ReflexaoDiaResponse>> buscarDeHoje() {
        return ResponseEntity.ok(ApiResponse.ok(reflexaoDiaService.buscarDeHoje()));
    }

    @GetMapping("/{mes}/{dia}")
    public ResponseEntity<ApiResponse<ReflexaoDiaResponse>> buscarPorData(@PathVariable int mes, @PathVariable int dia) {
        return ResponseEntity.ok(ApiResponse.ok(reflexaoDiaService.buscarPorData(mes, dia)));
    }
}

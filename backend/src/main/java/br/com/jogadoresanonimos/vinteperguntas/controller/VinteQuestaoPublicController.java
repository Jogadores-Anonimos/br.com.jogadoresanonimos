package br.com.jogadoresanonimos.vinteperguntas.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoResponse;
import br.com.jogadoresanonimos.vinteperguntas.service.VinteQuestaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vinte-perguntas")
public class VinteQuestaoPublicController {

    private final VinteQuestaoService vinteQuestaoService;

    public VinteQuestaoPublicController(VinteQuestaoService vinteQuestaoService) {
        this.vinteQuestaoService = vinteQuestaoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VinteQuestaoResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(vinteQuestaoService.listar()));
    }
}

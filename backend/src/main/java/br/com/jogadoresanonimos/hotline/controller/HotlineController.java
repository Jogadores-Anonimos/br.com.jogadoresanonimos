package br.com.jogadoresanonimos.hotline.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.hotline.dto.HotlineRegiaoResponse;
import br.com.jogadoresanonimos.hotline.service.HotlineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotlines")
public class HotlineController {

    private final HotlineService hotlineService;

    public HotlineController(HotlineService hotlineService) {
        this.hotlineService = hotlineService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HotlineRegiaoResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(hotlineService.listarTodas()));
    }
}

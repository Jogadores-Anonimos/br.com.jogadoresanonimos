package br.com.jogadoresanonimos.mural.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.mural.dto.BannerMuralPublicoResponse;
import br.com.jogadoresanonimos.mural.service.BannerMuralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mural-de-apoio")
public class MuralDeApoioController {

    private final BannerMuralService bannerMuralService;

    public MuralDeApoioController(BannerMuralService bannerMuralService) {
        this.bannerMuralService = bannerMuralService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerMuralPublicoResponse>>> listarAtivos() {
        return ResponseEntity.ok(ApiResponse.ok(bannerMuralService.listarAtivos()));
    }
}

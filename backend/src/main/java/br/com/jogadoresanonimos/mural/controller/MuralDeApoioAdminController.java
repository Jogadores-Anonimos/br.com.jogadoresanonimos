package br.com.jogadoresanonimos.mural.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.mural.dto.BannerMuralAdminResponse;
import br.com.jogadoresanonimos.mural.dto.CriarBannerMuralRequest;
import br.com.jogadoresanonimos.mural.dto.EditarBannerMuralRequest;
import br.com.jogadoresanonimos.mural.service.BannerMuralService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/mural-de-apoio")
@PreAuthorize("hasRole('ADMIN')")
public class MuralDeApoioAdminController {

    private final BannerMuralService bannerMuralService;

    public MuralDeApoioAdminController(BannerMuralService bannerMuralService) {
        this.bannerMuralService = bannerMuralService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerMuralAdminResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(bannerMuralService.listarTodos()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BannerMuralAdminResponse>> criar(@Valid @RequestBody CriarBannerMuralRequest request) {
        BannerMuralAdminResponse response = bannerMuralService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerMuralAdminResponse>> editar(@PathVariable UUID id,
                                                                          @Valid @RequestBody EditarBannerMuralRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(bannerMuralService.editar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID id) {
        bannerMuralService.remover(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Banner removido.")));
    }
}

package br.com.jogadoresanonimos.hotline.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.hotline.dto.*;
import br.com.jogadoresanonimos.hotline.service.HotlineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/hotlines")
@PreAuthorize("hasRole('ADMIN')")
public class HotlineAdminController {

    private final HotlineService hotlineService;

    public HotlineAdminController(HotlineService hotlineService) {
        this.hotlineService = hotlineService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HotlineRegiaoResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(hotlineService.listarTodas()));
    }

    @PostMapping("/regioes")
    public ResponseEntity<ApiResponse<HotlineRegiaoResponse>> criarRegiao(@Valid @RequestBody CriarRegiaoRequest request) {
        HotlineRegiaoResponse response = hotlineService.criarRegiao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/regioes/{id}")
    public ResponseEntity<ApiResponse<HotlineRegiaoResponse>> editarRegiao(@PathVariable UUID id,
                                                                              @Valid @RequestBody EditarRegiaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(hotlineService.editarRegiao(id, request)));
    }

    @DeleteMapping("/regioes/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> removerRegiao(@PathVariable UUID id) {
        hotlineService.removerRegiao(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Região removida.")));
    }

    @PostMapping("/regioes/{regiaoId}/telefones")
    public ResponseEntity<ApiResponse<HotlineRegiaoResponse>> adicionarTelefone(@PathVariable UUID regiaoId,
                                                                                  @Valid @RequestBody CriarTelefoneRequest request) {
        HotlineRegiaoResponse response = hotlineService.adicionarTelefone(regiaoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/telefones/{id}")
    public ResponseEntity<ApiResponse<HotlineTelefoneResponse>> editarTelefone(@PathVariable UUID id,
                                                                                  @Valid @RequestBody EditarTelefoneRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(hotlineService.editarTelefone(id, request)));
    }

    @DeleteMapping("/telefones/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> removerTelefone(@PathVariable UUID id) {
        hotlineService.removerTelefone(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Telefone removido.")));
    }
}

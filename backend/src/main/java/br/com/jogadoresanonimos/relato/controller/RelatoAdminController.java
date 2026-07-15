package br.com.jogadoresanonimos.relato.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.common.dto.PageResponse;
import br.com.jogadoresanonimos.relato.dto.EditarRelatoRequest;
import br.com.jogadoresanonimos.relato.dto.RelatoAdminResponse;
import br.com.jogadoresanonimos.relato.entity.StatusRelato;
import br.com.jogadoresanonimos.relato.service.RelatoService;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/relatos")
@PreAuthorize("hasRole('ADMIN')")
public class RelatoAdminController {

    private final RelatoService relatoService;

    public RelatoAdminController(RelatoService relatoService) {
        this.relatoService = relatoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RelatoAdminResponse>>> listarPorStatus(
            @RequestParam("status") StatusRelato status,
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(ApiResponse.ok(relatoService.listarPorStatus(status, pagina, tamanho)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RelatoAdminResponse>> editar(@PathVariable UUID id,
                                                                     @Valid @RequestBody EditarRelatoRequest request,
                                                                     @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.ok(relatoService.editar(id, request, principal.id())));
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<ApiResponse<RelatoAdminResponse>> aprovar(@PathVariable UUID id,
                                                                      @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.ok(relatoService.aprovar(id, principal.id())));
    }

    @PostMapping("/{id}/rejeitar")
    public ResponseEntity<ApiResponse<RelatoAdminResponse>> rejeitar(@PathVariable UUID id,
                                                                       @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.ok(relatoService.rejeitar(id, principal.id())));
    }

    @PostMapping("/{id}/reverter")
    public ResponseEntity<ApiResponse<RelatoAdminResponse>> reverter(@PathVariable UUID id,
                                                                       @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.ok(relatoService.reverter(id, principal.id())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID id,
                                                                   @AuthenticationPrincipal AuthenticatedUser principal) {
        relatoService.remover(id, principal.id());
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Relato removido.")));
    }
}

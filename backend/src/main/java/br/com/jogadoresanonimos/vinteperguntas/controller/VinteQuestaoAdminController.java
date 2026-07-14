package br.com.jogadoresanonimos.vinteperguntas.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoCriarRequest;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoResponse;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoUpdateRequest;
import br.com.jogadoresanonimos.vinteperguntas.service.VinteQuestaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/vinte-perguntas")
@PreAuthorize("hasRole('ADMIN')")
public class VinteQuestaoAdminController {

    private final VinteQuestaoService vinteQuestaoService;

    public VinteQuestaoAdminController(VinteQuestaoService vinteQuestaoService) {
        this.vinteQuestaoService = vinteQuestaoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VinteQuestaoResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(vinteQuestaoService.listar()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VinteQuestaoResponse>> criar(@Valid @RequestBody VinteQuestaoCriarRequest request,
                                                                     @AuthenticationPrincipal AuthenticatedUser principal) {
        VinteQuestaoResponse response = vinteQuestaoService.criar(request, principal.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VinteQuestaoResponse>> atualizar(@PathVariable UUID id,
                                                                        @Valid @RequestBody VinteQuestaoUpdateRequest request,
                                                                        @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.ok(vinteQuestaoService.atualizar(id, request, principal.id())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID id) {
        vinteQuestaoService.remover(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Pergunta removida.")));
    }

    @PostMapping("/{id}/mover-cima")
    public ResponseEntity<ApiResponse<VinteQuestaoResponse>> moverParaCima(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(vinteQuestaoService.moverParaCima(id)));
    }

    @PostMapping("/{id}/mover-baixo")
    public ResponseEntity<ApiResponse<VinteQuestaoResponse>> moverParaBaixo(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(vinteQuestaoService.moverParaBaixo(id)));
    }
}

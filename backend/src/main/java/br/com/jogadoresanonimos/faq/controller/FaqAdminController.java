package br.com.jogadoresanonimos.faq.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.faq.dto.CriarFaqRequest;
import br.com.jogadoresanonimos.faq.dto.EditarFaqRequest;
import br.com.jogadoresanonimos.faq.dto.FaqResponse;
import br.com.jogadoresanonimos.faq.service.FaqService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/faq")
@PreAuthorize("hasRole('ADMIN')")
public class FaqAdminController {

    private final FaqService faqService;

    public FaqAdminController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FaqResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(faqService.listarTodos()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FaqResponse>> criar(@Valid @RequestBody CriarFaqRequest request) {
        FaqResponse response = faqService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FaqResponse>> editar(@PathVariable UUID id,
                                                             @Valid @RequestBody EditarFaqRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(faqService.editar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID id) {
        faqService.remover(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Pergunta removida.")));
    }

    @PostMapping("/{id}/mover-cima")
    public ResponseEntity<ApiResponse<FaqResponse>> moverParaCima(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(faqService.moverParaCima(id)));
    }

    @PostMapping("/{id}/mover-baixo")
    public ResponseEntity<ApiResponse<FaqResponse>> moverParaBaixo(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(faqService.moverParaBaixo(id)));
    }
}

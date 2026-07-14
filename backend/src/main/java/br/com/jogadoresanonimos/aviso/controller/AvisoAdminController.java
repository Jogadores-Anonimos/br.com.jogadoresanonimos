package br.com.jogadoresanonimos.aviso.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.aviso.dto.AvisoAdminResponse;
import br.com.jogadoresanonimos.aviso.dto.CriarAvisoRequest;
import br.com.jogadoresanonimos.aviso.dto.EditarAvisoRequest;
import br.com.jogadoresanonimos.aviso.service.AvisoService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/avisos")
@PreAuthorize("hasRole('ADMIN')")
public class AvisoAdminController {

    private final AvisoService avisoService;

    public AvisoAdminController(AvisoService avisoService) {
        this.avisoService = avisoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AvisoAdminResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(avisoService.listarTodos()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AvisoAdminResponse>> criar(@Valid @RequestBody CriarAvisoRequest request) {
        AvisoAdminResponse response = avisoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AvisoAdminResponse>> editar(@PathVariable UUID id,
                                                                     @Valid @RequestBody EditarAvisoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(avisoService.editar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID id) {
        avisoService.remover(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Aviso removido.")));
    }
}

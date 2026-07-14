package br.com.jogadoresanonimos.reuniao.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoResponse;
import br.com.jogadoresanonimos.reuniao.service.ReuniaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reunioes")
@PreAuthorize("hasRole('ADMIN')")
public class ReuniaoAdminController {

    private final ReuniaoService reuniaoService;

    public ReuniaoAdminController(ReuniaoService reuniaoService) {
        this.reuniaoService = reuniaoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReuniaoResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(reuniaoService.listarTodas()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReuniaoResponse>> criar(@Valid @RequestBody CriarReuniaoRequest request) {
        ReuniaoResponse response = reuniaoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReuniaoResponse>> editar(@PathVariable UUID id,
                                                                 @Valid @RequestBody EditarReuniaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(reuniaoService.editar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID id) {
        reuniaoService.remover(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Reunião removida.")));
    }
}

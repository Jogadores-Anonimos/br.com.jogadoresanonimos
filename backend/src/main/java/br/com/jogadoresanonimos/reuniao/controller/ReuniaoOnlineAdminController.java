package br.com.jogadoresanonimos.reuniao.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineResponse;
import br.com.jogadoresanonimos.reuniao.service.ReuniaoOnlineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reunioes-online")
@PreAuthorize("hasRole('ADMIN')")
public class ReuniaoOnlineAdminController {

    private final ReuniaoOnlineService reuniaoOnlineService;

    public ReuniaoOnlineAdminController(ReuniaoOnlineService reuniaoOnlineService) {
        this.reuniaoOnlineService = reuniaoOnlineService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReuniaoOnlineResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(reuniaoOnlineService.listarTodas()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReuniaoOnlineResponse>> criar(@Valid @RequestBody CriarReuniaoOnlineRequest request) {
        ReuniaoOnlineResponse response = reuniaoOnlineService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReuniaoOnlineResponse>> editar(@PathVariable UUID id,
                                                                       @Valid @RequestBody EditarReuniaoOnlineRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(reuniaoOnlineService.editar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID id) {
        reuniaoOnlineService.remover(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Reunião online removida.")));
    }
}

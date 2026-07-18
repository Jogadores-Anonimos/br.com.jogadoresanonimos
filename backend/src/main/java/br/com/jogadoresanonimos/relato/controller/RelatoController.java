package br.com.jogadoresanonimos.relato.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.common.dto.PageResponse;
import br.com.jogadoresanonimos.relato.dto.EnviarRelatoRequest;
import br.com.jogadoresanonimos.relato.dto.RelatoEnvioResponse;
import br.com.jogadoresanonimos.relato.dto.RelatoPublicoListaResponse;
import br.com.jogadoresanonimos.relato.dto.RelatoPublicoResponse;
import br.com.jogadoresanonimos.relato.service.RelatoService;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/relatos")
public class RelatoController {

    private final RelatoService relatoService;

    public RelatoController(RelatoService relatoService) {
        this.relatoService = relatoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RelatoEnvioResponse>> enviar(@Valid @RequestBody EnviarRelatoRequest request,
                                                                     @AuthenticationPrincipal AuthenticatedUser principal) {
        RelatoEnvioResponse response = relatoService.enviar(request, principal.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RelatoPublicoListaResponse>>> listarAprovados(
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(ApiResponse.ok(relatoService.listarAprovados(pagina, tamanho)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RelatoPublicoResponse>> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(relatoService.buscarAprovadoPorId(id)));
    }
}

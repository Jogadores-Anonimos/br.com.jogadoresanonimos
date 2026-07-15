package br.com.jogadoresanonimos.escritorio.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.escritorio.dto.*;
import br.com.jogadoresanonimos.escritorio.service.EscritorioRegionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/escritorios-regionais")
@PreAuthorize("hasRole('ADMIN')")
public class EscritorioRegionalAdminController {

    private final EscritorioRegionalService escritorioRegionalService;

    public EscritorioRegionalAdminController(EscritorioRegionalService escritorioRegionalService) {
        this.escritorioRegionalService = escritorioRegionalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<EscritoriosRegionaisResponse>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.listarEstruturaCompleta()));
    }

    @PutMapping("/regioes/{id}")
    public ResponseEntity<ApiResponse<RegiaoIntergrupoResponse>> editarRegiao(
            @PathVariable UUID id, @Valid @RequestBody EditarRegiaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.editarRegiao(id, request)));
    }

    @PostMapping("/regioes/{id}/mover-cima")
    public ResponseEntity<ApiResponse<RegiaoIntergrupoResponse>> moverRegiaoParaCima(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.moverRegiaoParaCima(id)));
    }

    @PostMapping("/regioes/{id}/mover-baixo")
    public ResponseEntity<ApiResponse<RegiaoIntergrupoResponse>> moverRegiaoParaBaixo(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.moverRegiaoParaBaixo(id)));
    }

    @PostMapping("/regioes/{regiaoId}/estados")
    public ResponseEntity<ApiResponse<EstadoIntergrupoResponse>> criarEstado(
            @PathVariable UUID regiaoId, @Valid @RequestBody CriarEstadoRequest request) {
        EstadoIntergrupoResponse response = escritorioRegionalService.criarEstado(regiaoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/regioes/{regiaoId}/estados/{id}")
    public ResponseEntity<ApiResponse<EstadoIntergrupoResponse>> editarEstado(
            @PathVariable UUID regiaoId, @PathVariable UUID id, @Valid @RequestBody EditarEstadoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.editarEstado(regiaoId, id, request)));
    }

    @DeleteMapping("/regioes/{regiaoId}/estados/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> removerEstado(
            @PathVariable UUID regiaoId, @PathVariable UUID id) {
        escritorioRegionalService.removerEstado(regiaoId, id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Estado removido.")));
    }

    @PostMapping("/regioes/{regiaoId}/estados/{id}/mover-cima")
    public ResponseEntity<ApiResponse<EstadoIntergrupoResponse>> moverEstadoParaCima(
            @PathVariable UUID regiaoId, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.moverEstadoParaCima(regiaoId, id)));
    }

    @PostMapping("/regioes/{regiaoId}/estados/{id}/mover-baixo")
    public ResponseEntity<ApiResponse<EstadoIntergrupoResponse>> moverEstadoParaBaixo(
            @PathVariable UUID regiaoId, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.moverEstadoParaBaixo(regiaoId, id)));
    }

    @PostMapping("/regioes/{regiaoId}/enderecos")
    public ResponseEntity<ApiResponse<GrupoIntergrupoResponse>> criarEnderecoGrupo(
            @PathVariable UUID regiaoId, @Valid @RequestBody CriarEnderecoGrupoRequest request) {
        GrupoIntergrupoResponse response = escritorioRegionalService.criarEnderecoGrupo(regiaoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/estados/{estadoId}/grupos")
    public ResponseEntity<ApiResponse<GrupoIntergrupoResponse>> criarGrupo(
            @PathVariable UUID estadoId, @Valid @RequestBody CriarGrupoRequest request) {
        GrupoIntergrupoResponse response = escritorioRegionalService.criarGrupo(estadoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/estados/{estadoId}/grupos/{id}")
    public ResponseEntity<ApiResponse<GrupoIntergrupoResponse>> editarGrupo(
            @PathVariable UUID estadoId, @PathVariable UUID id, @Valid @RequestBody EditarGrupoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.editarGrupo(estadoId, id, request)));
    }

    @DeleteMapping("/estados/{estadoId}/grupos/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> removerGrupo(
            @PathVariable UUID estadoId, @PathVariable UUID id) {
        escritorioRegionalService.removerGrupo(estadoId, id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Grupo removido.")));
    }

    @PostMapping("/estados/{estadoId}/grupos/{id}/mover-cima")
    public ResponseEntity<ApiResponse<GrupoIntergrupoResponse>> moverGrupoParaCima(
            @PathVariable UUID estadoId, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.moverGrupoParaCima(estadoId, id)));
    }

    @PostMapping("/estados/{estadoId}/grupos/{id}/mover-baixo")
    public ResponseEntity<ApiResponse<GrupoIntergrupoResponse>> moverGrupoParaBaixo(
            @PathVariable UUID estadoId, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.moverGrupoParaBaixo(estadoId, id)));
    }

    @PostMapping("/escritorios-servico")
    public ResponseEntity<ApiResponse<EscritorioServicoResponse>> criarEscritorioServico(
            @Valid @RequestBody CriarEscritorioServicoRequest request) {
        EscritorioServicoResponse response = escritorioRegionalService.criarEscritorioServico(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/escritorios-servico/{id}")
    public ResponseEntity<ApiResponse<EscritorioServicoResponse>> editarEscritorioServico(
            @PathVariable UUID id, @Valid @RequestBody EditarEscritorioServicoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.editarEscritorioServico(id, request)));
    }

    @DeleteMapping("/escritorios-servico/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> removerEscritorioServico(@PathVariable UUID id) {
        escritorioRegionalService.removerEscritorioServico(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Escritório de serviço removido.")));
    }

    @PostMapping("/escritorios-servico/{id}/mover-cima")
    public ResponseEntity<ApiResponse<EscritorioServicoResponse>> moverEscritorioServicoParaCima(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.moverEscritorioServicoParaCima(id)));
    }

    @PostMapping("/escritorios-servico/{id}/mover-baixo")
    public ResponseEntity<ApiResponse<EscritorioServicoResponse>> moverEscritorioServicoParaBaixo(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(escritorioRegionalService.moverEscritorioServicoParaBaixo(id)));
    }
}

package br.com.jogadoresanonimos.auditoria.controller;

import br.com.jogadoresanonimos.auditoria.dto.AuditoriaLogResponse;
import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.service.AdminAuditoriaService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.common.dto.PageResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/admin/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditoriaController {

    private final AdminAuditoriaService adminAuditoriaService;

    public AdminAuditoriaController(AdminAuditoriaService adminAuditoriaService) {
        this.adminAuditoriaService = adminAuditoriaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditoriaLogResponse>>> listar(
            @RequestParam(value = "entidade", required = false) String entidade,
            @RequestParam(value = "acao", required = false) AcaoAuditoria acao,
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dataFim,
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(ApiResponse.ok(
                adminAuditoriaService.listar(entidade, acao, dataInicio, dataFim, pagina, tamanho)));
    }
}

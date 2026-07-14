package br.com.jogadoresanonimos.reflexao.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaDoMesResponse;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaResponse;
import br.com.jogadoresanonimos.reflexao.dto.SalvarReflexaoDiaRequest;
import br.com.jogadoresanonimos.reflexao.service.ReflexaoDiaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reflexao-do-dia")
@PreAuthorize("hasRole('ADMIN')")
public class ReflexaoDiaAdminController {

    private final ReflexaoDiaService reflexaoDiaService;

    public ReflexaoDiaAdminController(ReflexaoDiaService reflexaoDiaService) {
        this.reflexaoDiaService = reflexaoDiaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReflexaoDiaDoMesResponse>>> listarMes(@RequestParam int mes) {
        return ResponseEntity.ok(ApiResponse.ok(reflexaoDiaService.listarMes(mes)));
    }

    @PutMapping("/{mes}/{dia}")
    public ResponseEntity<ApiResponse<ReflexaoDiaResponse>> salvar(@PathVariable int mes, @PathVariable int dia,
                                                                     @Valid @RequestBody SalvarReflexaoDiaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(reflexaoDiaService.salvar(mes, dia, request)));
    }
}

package br.com.jogadoresanonimos.cms.controller;

import br.com.jogadoresanonimos.cms.dto.*;
import br.com.jogadoresanonimos.cms.service.CmsService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/cms/paginas")
@PreAuthorize("hasRole('ADMIN')")
public class CmsAdminController {

    private final CmsService cmsService;

    public CmsAdminController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CmsPaginaListItemResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(cmsService.listarAdmin()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CmsPaginaAdminResponse>> buscar(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(cmsService.buscarAdmin(slug)));
    }

    @PutMapping("/{slug}")
    public ResponseEntity<ApiResponse<CmsPaginaAdminResponse>> atualizar(@PathVariable String slug,
                                                                          @Valid @RequestBody CmsPaginaUpdateRequest request,
                                                                          @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.ok(cmsService.atualizar(slug, request, principal.id())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CmsPaginaAdminResponse>> criar(@Valid @RequestBody CmsPaginaCreateRequest request,
                                                                      @AuthenticationPrincipal AuthenticatedUser principal) {
        CmsPaginaAdminResponse response = cmsService.criar(request, principal.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }
}

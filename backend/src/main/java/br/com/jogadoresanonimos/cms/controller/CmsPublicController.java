package br.com.jogadoresanonimos.cms.controller;

import br.com.jogadoresanonimos.cms.dto.CmsPaginaPublicaResponse;
import br.com.jogadoresanonimos.cms.service.CmsService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cms")
public class CmsPublicController {

    private final CmsService cmsService;

    public CmsPublicController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @GetMapping("/paginas/{slug}")
    public ResponseEntity<ApiResponse<CmsPaginaPublicaResponse>> buscar(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(cmsService.buscarPublica(slug)));
    }
}

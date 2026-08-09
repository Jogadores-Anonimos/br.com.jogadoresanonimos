package br.com.jogadoresanonimos.auth.controller;

import br.com.jogadoresanonimos.auth.dto.AdminRedefinirSenhaRequest;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.auth.service.AdminAuthService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<ApiResponse<MensagemResponse>> redefinirSenha(@Valid @RequestBody AdminRedefinirSenhaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(adminAuthService.redefinirSenha(request)));
    }
}

package br.com.jogadoresanonimos.auth.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.auth.dto.MfaConfirmarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaDesativarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaEnrollResponse;
import br.com.jogadoresanonimos.auth.service.MfaService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/mfa")
@PreAuthorize("hasRole('ADMIN')")
public class MfaAdminController {

    private final MfaService mfaService;

    public MfaAdminController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<MfaEnrollResponse>> enroll(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.ok(mfaService.enroll(principal.id())));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<ApiResponse<MensagemResponse>> confirmar(@AuthenticationPrincipal AuthenticatedUser principal,
                                                                     @Valid @RequestBody MfaConfirmarRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(mfaService.confirmar(principal.id(), request)));
    }

    @PostMapping("/desativar")
    public ResponseEntity<ApiResponse<MensagemResponse>> desativar(@AuthenticationPrincipal AuthenticatedUser principal,
                                                                      @Valid @RequestBody MfaDesativarRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(mfaService.desativar(principal.id(), request)));
    }
}

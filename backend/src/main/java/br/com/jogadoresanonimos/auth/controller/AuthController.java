package br.com.jogadoresanonimos.auth.controller;

import br.com.jogadoresanonimos.auth.dto.*;
import br.com.jogadoresanonimos.auth.service.AuthService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import br.com.jogadoresanonimos.usuario.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<UsuarioResponse>> registrar(@Valid @RequestBody RegistroRequest request) {
        UsuarioResponse response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @PostMapping("/login/mfa")
    public ResponseEntity<ApiResponse<LoginResponse>> loginComMfa(@Valid @RequestBody MfaLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.loginComMfa(request)));
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<ApiResponse<MensagemResponse>> esqueciSenha(@Valid @RequestBody EsqueciSenhaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.esqueciSenha(request)));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<ApiResponse<MensagemResponse>> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.redefinirSenha(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MensagemResponse>> logout(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.ok(authService.logout(principal)));
    }
}

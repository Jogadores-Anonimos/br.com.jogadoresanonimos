package br.com.jogadoresanonimos.usuario.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import br.com.jogadoresanonimos.usuario.dto.AlterarSenhaRequest;
import br.com.jogadoresanonimos.usuario.dto.AtualizarContaRequest;
import br.com.jogadoresanonimos.usuario.dto.UsuarioMeResponse;
import br.com.jogadoresanonimos.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioMeResponse>> me(@AuthenticationPrincipal AuthenticatedUser principal) {
        if (principal == null) {
            throw new UnauthenticatedException(ErrorCode.AUTH_TOKEN_AUSENTE_OU_INVALIDO, "Token ausente ou inválido");
        }
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.buscarMe(principal.id())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioMeResponse>> atualizarConta(
            @Valid @RequestBody AtualizarContaRequest request,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        if (principal == null) {
            throw new UnauthenticatedException(ErrorCode.AUTH_TOKEN_AUSENTE_OU_INVALIDO, "Token ausente ou inválido");
        }
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.atualizarConta(principal.id(), request)));
    }

    @PutMapping("/me/senha")
    public ResponseEntity<ApiResponse<MensagemResponse>> alterarSenha(
            @Valid @RequestBody AlterarSenhaRequest request,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        if (principal == null) {
            throw new UnauthenticatedException(ErrorCode.AUTH_TOKEN_AUSENTE_OU_INVALIDO, "Token ausente ou inválido");
        }
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.alterarSenha(principal.id(), request)));
    }
}

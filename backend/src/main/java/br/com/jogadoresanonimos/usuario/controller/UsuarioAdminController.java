package br.com.jogadoresanonimos.usuario.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import br.com.jogadoresanonimos.usuario.dto.EditarRoleRequest;
import br.com.jogadoresanonimos.usuario.dto.UsuarioAdminResponse;
import br.com.jogadoresanonimos.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioAdminResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.listarTodos()));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UsuarioAdminResponse>> editarRole(@PathVariable UUID id,
                                                                          @Valid @RequestBody EditarRoleRequest request,
                                                                          @AuthenticationPrincipal AuthenticatedUser principal) {
        UsuarioAdminResponse response = usuarioService.editarRole(id, request.role(), principal.id());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

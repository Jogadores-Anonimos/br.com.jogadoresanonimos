package br.com.jogadoresanonimos.usuario.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import br.com.jogadoresanonimos.usuario.dto.EditarRoleRequest;
import br.com.jogadoresanonimos.usuario.dto.UsuarioAdminResponse;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioAdminControllerTest {

    @Mock
    UsuarioService usuarioService;

    UsuarioAdminController usuarioAdminController;

    @BeforeEach
    void setUp() {
        usuarioAdminController = new UsuarioAdminController(usuarioService);
    }

    private UsuarioAdminResponse usuarioAdminResponse(UUID id) {
        return new UsuarioAdminResponse(id, "apelido", "email@exemplo.com", Role.PARTICIPANTE, Instant.now());
    }

    @Test
    void listarTodos_deveRetornarOkComListaDoService() {
        UsuarioAdminResponse item = usuarioAdminResponse(UUID.randomUUID());
        when(usuarioService.listarTodos()).thenReturn(List.of(item));

        ResponseEntity<ApiResponse<List<UsuarioAdminResponse>>> resposta = usuarioAdminController.listarTodos();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).containsExactly(item);
    }

    @Test
    void editarRole_deveRetornarOkComRespostaDoServiceEUsarIdDoPrincipal() {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(solicitanteId, "admin", "ADMIN");
        EditarRoleRequest request = new EditarRoleRequest(Role.ADMIN);
        UsuarioAdminResponse esperado = usuarioAdminResponse(id);
        when(usuarioService.editarRole(id, Role.ADMIN, solicitanteId)).thenReturn(esperado);

        ResponseEntity<ApiResponse<UsuarioAdminResponse>> resposta =
                usuarioAdminController.editarRole(id, request, principal);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(usuarioService).editarRole(id, Role.ADMIN, solicitanteId);
    }
}

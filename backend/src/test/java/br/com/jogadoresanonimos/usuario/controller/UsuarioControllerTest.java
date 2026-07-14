package br.com.jogadoresanonimos.usuario.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import br.com.jogadoresanonimos.usuario.dto.AlterarSenhaRequest;
import br.com.jogadoresanonimos.usuario.dto.AtualizarContaRequest;
import br.com.jogadoresanonimos.usuario.dto.UsuarioMeResponse;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    UsuarioService usuarioService;

    UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        usuarioController = new UsuarioController(usuarioService);
    }

    @Test
    void me_deveLancarUnauthenticatedQuandoPrincipalNulo() {
        assertThatThrownBy(() -> usuarioController.me(null))
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void atualizarConta_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(id, "apelido", "PARTICIPANTE");
        AtualizarContaRequest request = new AtualizarContaRequest("novoApelido", "novo@exemplo.com");
        UsuarioMeResponse esperado = new UsuarioMeResponse(id, "novoApelido", "novo@exemplo.com", Role.PARTICIPANTE, Instant.now());
        when(usuarioService.atualizarConta(id, request)).thenReturn(esperado);

        ResponseEntity<ApiResponse<UsuarioMeResponse>> resposta = usuarioController.atualizarConta(request, principal);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(usuarioService).atualizarConta(id, request);
    }

    @Test
    void atualizarConta_deveLancarUnauthenticatedQuandoPrincipalNulo() {
        AtualizarContaRequest request = new AtualizarContaRequest("apelido", "email@exemplo.com");

        assertThatThrownBy(() -> usuarioController.atualizarConta(request, null))
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void alterarSenha_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(id, "apelido", "PARTICIPANTE");
        AlterarSenhaRequest request = new AlterarSenhaRequest("senhaAtual123", "senhaNova12345", "senhaNova12345");
        MensagemResponse esperado = new MensagemResponse("Senha alterada com sucesso.");
        when(usuarioService.alterarSenha(id, request)).thenReturn(esperado);

        ResponseEntity<ApiResponse<MensagemResponse>> resposta = usuarioController.alterarSenha(request, principal);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(usuarioService).alterarSenha(id, request);
    }

    @Test
    void alterarSenha_deveLancarUnauthenticatedQuandoPrincipalNulo() {
        AlterarSenhaRequest request = new AlterarSenhaRequest("senhaAtual123", "senhaNova12345", "senhaNova12345");

        assertThatThrownBy(() -> usuarioController.alterarSenha(request, null))
                .isInstanceOf(UnauthenticatedException.class);
    }
}

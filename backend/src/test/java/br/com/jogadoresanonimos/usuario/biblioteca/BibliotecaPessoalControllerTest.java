package br.com.jogadoresanonimos.usuario.biblioteca;

import br.com.jogadoresanonimos.arquivo.dto.ArquivoResponse;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import br.com.jogadoresanonimos.usuario.biblioteca.controller.BibliotecaPessoalController;
import br.com.jogadoresanonimos.usuario.biblioteca.service.BibliotecaPessoalService;
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
class BibliotecaPessoalControllerTest {

    @Mock
    BibliotecaPessoalService bibliotecaPessoalService;

    BibliotecaPessoalController controller;

    UUID usuarioId;
    AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        controller = new BibliotecaPessoalController(bibliotecaPessoalService);
        usuarioId = UUID.randomUUID();
        principal = new AuthenticatedUser(usuarioId, "apelido", "USER");
    }

    @Test
    void listar_deveRetornarOkComListaDoService() {
        ArquivoResponse item = new ArquivoResponse(UUID.randomUUID(), "livro.pdf", 1000, "Título", "descrição",
                "/api/v1/arquivos/x/download", null, Instant.now());
        when(bibliotecaPessoalService.listar(usuarioId)).thenReturn(List.of(item));
        ResponseEntity<ApiResponse<List<ArquivoResponse>>> resposta = controller.listar(principal);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        assertThat(resposta.getBody().data()).containsExactly(item);
    }

    @Test
    void adicionar_deveChamarServiceComUsuarioDoTokenERetornarOk() {
        UUID arquivoId = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = controller.adicionar(arquivoId, principal);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(bibliotecaPessoalService).adicionar(usuarioId, arquivoId);
    }

    @Test
    void remover_deveChamarServiceComUsuarioDoTokenERetornarOk() {
        UUID arquivoId = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = controller.remover(arquivoId, principal);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(bibliotecaPessoalService).remover(usuarioId, arquivoId);
    }
}

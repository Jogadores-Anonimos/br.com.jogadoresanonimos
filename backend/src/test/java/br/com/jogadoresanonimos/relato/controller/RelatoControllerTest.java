package br.com.jogadoresanonimos.relato.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.common.dto.PageResponse;
import br.com.jogadoresanonimos.relato.dto.EnviarRelatoRequest;
import br.com.jogadoresanonimos.relato.dto.RelatoEnvioResponse;
import br.com.jogadoresanonimos.relato.dto.RelatoPublicoResponse;
import br.com.jogadoresanonimos.relato.service.RelatoService;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
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
class RelatoControllerTest {

    @Mock
    RelatoService relatoService;

    RelatoController relatoController;

    @BeforeEach
    void setUp() {
        relatoController = new RelatoController(relatoService);
    }

    @Test
    void enviar_deveRetornarCreatedComRespostaDoService() {
        UUID autorId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(autorId, "apelido", "USER");
        EnviarRelatoRequest request = new EnviarRelatoRequest("x".repeat(60), "Anônimo");
        RelatoEnvioResponse esperado = new RelatoEnvioResponse(UUID.randomUUID(), "pending");
        when(relatoService.enviar(request, autorId)).thenReturn(esperado);

        ResponseEntity<ApiResponse<RelatoEnvioResponse>> resposta = relatoController.enviar(request, principal);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().success()).isTrue();
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(relatoService).enviar(request, autorId);
    }

    @Test
    void listarAprovados_deveRetornarOkComPaginaDoService() {
        RelatoPublicoResponse item = new RelatoPublicoResponse(UUID.randomUUID(), "texto".repeat(20), "Anônimo", Instant.now());
        PageResponse<RelatoPublicoResponse> pagina = new PageResponse<>(List.of(item), 0, 20, 1, 1);
        when(relatoService.listarAprovados(0, 20)).thenReturn(pagina);

        ResponseEntity<ApiResponse<PageResponse<RelatoPublicoResponse>>> resposta = relatoController.listarAprovados(0, 20);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(pagina);
    }
}

package br.com.jogadoresanonimos.relato.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.common.dto.PageResponse;
import br.com.jogadoresanonimos.relato.dto.EditarRelatoRequest;
import br.com.jogadoresanonimos.relato.dto.RelatoAdminResponse;
import br.com.jogadoresanonimos.relato.entity.StatusRelato;
import br.com.jogadoresanonimos.relato.service.RelatoService;
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
class RelatoAdminControllerTest {

    @Mock
    RelatoService relatoService;

    RelatoAdminController relatoAdminController;

    @BeforeEach
    void setUp() {
        relatoAdminController = new RelatoAdminController(relatoService);
    }

    private RelatoAdminResponse relatoAdminResponse(UUID id, String status) {
        return new RelatoAdminResponse(id, "texto".repeat(20), "Anônimo", status, Instant.now(), Instant.now());
    }

    @Test
    void listarPorStatus_deveRetornarOkComPaginaDoService() {
        RelatoAdminResponse item = relatoAdminResponse(UUID.randomUUID(), "pending");
        PageResponse<RelatoAdminResponse> pagina = new PageResponse<>(List.of(item), 0, 20, 1, 1);
        when(relatoService.listarPorStatus(StatusRelato.pending, 0, 20)).thenReturn(pagina);

        ResponseEntity<ApiResponse<PageResponse<RelatoAdminResponse>>> resposta =
                relatoAdminController.listarPorStatus(StatusRelato.pending, 0, 20);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(pagina);
    }

    @Test
    void editar_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EditarRelatoRequest request = new EditarRelatoRequest("texto editado".repeat(5));
        RelatoAdminResponse esperado = relatoAdminResponse(id, "pending");
        when(relatoService.editar(id, request)).thenReturn(esperado);

        ResponseEntity<ApiResponse<RelatoAdminResponse>> resposta = relatoAdminController.editar(id, request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(relatoService).editar(id, request);
    }

    @Test
    void aprovar_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        RelatoAdminResponse esperado = relatoAdminResponse(id, "approved");
        when(relatoService.aprovar(id)).thenReturn(esperado);

        ResponseEntity<ApiResponse<RelatoAdminResponse>> resposta = relatoAdminController.aprovar(id);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data().status()).isEqualTo("approved");
    }

    @Test
    void rejeitar_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        RelatoAdminResponse esperado = relatoAdminResponse(id, "rejected");
        when(relatoService.rejeitar(id)).thenReturn(esperado);

        ResponseEntity<ApiResponse<RelatoAdminResponse>> resposta = relatoAdminController.rejeitar(id);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data().status()).isEqualTo("rejected");
    }

    @Test
    void reverter_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        RelatoAdminResponse esperado = relatoAdminResponse(id, "pending");
        when(relatoService.reverter(id)).thenReturn(esperado);

        ResponseEntity<ApiResponse<RelatoAdminResponse>> resposta = relatoAdminController.reverter(id);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data().status()).isEqualTo("pending");
    }

    @Test
    void remover_deveRetornarOkEChamarServiceComId() {
        UUID id = UUID.randomUUID();

        ResponseEntity<ApiResponse<MensagemResponse>> resposta = relatoAdminController.remover(id);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(relatoService).remover(id);
    }
}

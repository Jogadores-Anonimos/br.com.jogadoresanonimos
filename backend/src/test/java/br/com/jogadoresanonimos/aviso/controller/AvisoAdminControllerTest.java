package br.com.jogadoresanonimos.aviso.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.aviso.dto.AvisoAdminResponse;
import br.com.jogadoresanonimos.aviso.dto.CriarAvisoRequest;
import br.com.jogadoresanonimos.aviso.dto.EditarAvisoRequest;
import br.com.jogadoresanonimos.aviso.service.AvisoService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvisoAdminControllerTest {

    @Mock
    AvisoService avisoService;

    AvisoAdminController avisoAdminController;

    @BeforeEach
    void setUp() {
        avisoAdminController = new AvisoAdminController(avisoService);
    }

    private AvisoAdminResponse avisoAdminResponse(UUID id) {
        return new AvisoAdminResponse(id, "titulo", "corpo", Instant.now(), false, null, false);
    }

    @Test
    void listarTodos_deveRetornarOkComListaDoService() {
        AvisoAdminResponse item = avisoAdminResponse(UUID.randomUUID());
        when(avisoService.listarTodos()).thenReturn(List.of(item));
        ResponseEntity<ApiResponse<List<AvisoAdminResponse>>> resposta = avisoAdminController.listarTodos();
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).containsExactly(item);
    }

    @Test
    void criar_deveRetornarCreatedComRespostaDoService() {
        CriarAvisoRequest request = new CriarAvisoRequest("titulo", "corpo", true, Instant.now().plus(1, ChronoUnit.DAYS));
        AvisoAdminResponse esperado = avisoAdminResponse(UUID.randomUUID());
        when(avisoService.criar(request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<AvisoAdminResponse>> resposta = avisoAdminController.criar(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(avisoService).criar(request);
    }

    @Test
    void editar_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EditarAvisoRequest request = new EditarAvisoRequest("titulo editado", "corpo editado", false, null);
        AvisoAdminResponse esperado = avisoAdminResponse(id);
        when(avisoService.editar(id, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<AvisoAdminResponse>> resposta = avisoAdminController.editar(id, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(avisoService).editar(id, request);
    }

    @Test
    void remover_deveRetornarOkEChamarServiceComId() {
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = avisoAdminController.remover(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(avisoService).remover(id);
    }
}

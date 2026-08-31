package br.com.jogadoresanonimos.reuniao.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineAgendaItemRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineAgendaItemResponse;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineResponse;
import br.com.jogadoresanonimos.reuniao.service.ReuniaoOnlineService;
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
class ReuniaoOnlineAdminControllerTest {

    @Mock
    ReuniaoOnlineService reuniaoOnlineService;

    ReuniaoOnlineAdminController reuniaoOnlineAdminController;

    @BeforeEach
    void setUp() {
        reuniaoOnlineAdminController = new ReuniaoOnlineAdminController(reuniaoOnlineService);
    }

    private ReuniaoOnlineResponse reuniaoOnlineResponse(UUID id) {
        return new ReuniaoOnlineResponse(id, "Grupo Online Quinta", "5511994721933",
                List.of(new ReuniaoOnlineAgendaItemResponse("QUINTA", "20h00")), Instant.now());
    }

    private CriarReuniaoOnlineRequest criarRequest() {
        return new CriarReuniaoOnlineRequest("Grupo Online Quinta", "5511994721933",
                List.of(new ReuniaoOnlineAgendaItemRequest("QUINTA", "20h00")));
    }

    private EditarReuniaoOnlineRequest editarRequest() {
        return new EditarReuniaoOnlineRequest("Grupo Online Quinta editado", null,
                List.of(new ReuniaoOnlineAgendaItemRequest("SABADO", "10h00")));
    }

    @Test
    void listarTodas_deveRetornarOkComListaDoService() {
        ReuniaoOnlineResponse item = reuniaoOnlineResponse(UUID.randomUUID());
        when(reuniaoOnlineService.listarTodas()).thenReturn(List.of(item));
        ResponseEntity<ApiResponse<List<ReuniaoOnlineResponse>>> resposta = reuniaoOnlineAdminController.listarTodas();
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).containsExactly(item);
    }

    @Test
    void criar_deveRetornarCreatedComRespostaDoService() {
        CriarReuniaoOnlineRequest request = criarRequest();
        ReuniaoOnlineResponse esperado = reuniaoOnlineResponse(UUID.randomUUID());
        when(reuniaoOnlineService.criar(request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<ReuniaoOnlineResponse>> resposta = reuniaoOnlineAdminController.criar(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(reuniaoOnlineService).criar(request);
    }

    @Test
    void editar_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EditarReuniaoOnlineRequest request = editarRequest();
        ReuniaoOnlineResponse esperado = reuniaoOnlineResponse(id);
        when(reuniaoOnlineService.editar(id, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<ReuniaoOnlineResponse>> resposta = reuniaoOnlineAdminController.editar(id, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(reuniaoOnlineService).editar(id, request);
    }

    @Test
    void remover_deveRetornarOkEChamarServiceComId() {
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = reuniaoOnlineAdminController.remover(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(reuniaoOnlineService).remover(id);
    }
}

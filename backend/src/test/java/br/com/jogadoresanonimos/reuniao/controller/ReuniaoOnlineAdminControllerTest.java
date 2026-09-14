package br.com.jogadoresanonimos.reuniao.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineAgendaItemRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineAgendaItemResponse;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineResponse;
import br.com.jogadoresanonimos.reuniao.entity.CorReuniaoOnline;
import br.com.jogadoresanonimos.reuniao.service.ReuniaoOnlineService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoOnlineAdminControllerTest {

    @Mock
    ReuniaoOnlineService reuniaoOnlineService;

    ReuniaoOnlineAdminController reuniaoOnlineAdminController;

    Validator validator;

    @BeforeEach
    void setUp() {
        reuniaoOnlineAdminController = new ReuniaoOnlineAdminController(reuniaoOnlineService);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private List<ReuniaoOnlineAgendaItemRequest> agendaRequest() {
        return List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h30"));
    }

    private List<ReuniaoOnlineAgendaItemResponse> agendaResponse() {
        return List.of(new ReuniaoOnlineAgendaItemResponse("SEGUNDA", "19h30"));
    }

    private ReuniaoOnlineResponse reuniaoOnlineResponse(UUID id) {
        return new ReuniaoOnlineResponse(id, "Grupo Salvador", "21 99472-1933", "descrição", null, null, null,
                CorReuniaoOnline.ROXO, agendaResponse(), Instant.now());
    }

    private CriarReuniaoOnlineRequest criarRequest() {
        return new CriarReuniaoOnlineRequest("Grupo Salvador", null, "descrição", null, null, null, agendaRequest());
    }

    private EditarReuniaoOnlineRequest editarRequest() {
        return new EditarReuniaoOnlineRequest("Grupo Salvador editado", null, "descrição editada", null, null, null,
                agendaRequest());
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
    void criar_deveRetornarCreatedComRespostaDoServiceIncluindoDescricao() {
        CriarReuniaoOnlineRequest request = criarRequest();
        ReuniaoOnlineResponse esperado = reuniaoOnlineResponse(UUID.randomUUID());
        when(reuniaoOnlineService.criar(request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<ReuniaoOnlineResponse>> resposta = reuniaoOnlineAdminController.criar(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        assertThat(resposta.getBody().data().descricao()).isEqualTo("descrição");
        assertThat(resposta.getBody().data().telefone()).isEqualTo("21 99472-1933");
        verify(reuniaoOnlineService).criar(request);
    }

    @Test
    void criar_comNomeEmBranco_deveFalharValidacao() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest(" ", null, "descrição", null, null, null,
                agendaRequest());
        Set<ConstraintViolation<CriarReuniaoOnlineRequest>> violacoes = validator.validate(request);
        assertThat(violacoes)
                .anyMatch(violacao -> violacao.getPropertyPath().toString().equals("nome"));
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

package br.com.jogadoresanonimos.reuniao.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoResponse;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.service.ReuniaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoAdminControllerTest {

    @Mock
    ReuniaoService reuniaoService;

    ReuniaoAdminController reuniaoAdminController;

    @BeforeEach
    void setUp() {
        reuniaoAdminController = new ReuniaoAdminController(reuniaoService);
    }

    private ReuniaoResponse reuniaoResponse(UUID id) {
        return new ReuniaoResponse(id, "Grupo Centro", CategoriaReuniao.presencial, "descricao",
                "Segunda-feira", "19h30", "endereco", "5599999999999", "Segunda", "Noite", -23.5505, -46.6333);
    }

    private CriarReuniaoRequest criarRequest() {
        return new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial, "descricao",
                "Segunda-feira", "19h30", "endereco", "5599999999999", "Segunda", "Noite", -23.5505, -46.6333);
    }

    private EditarReuniaoRequest editarRequest() {
        return new EditarReuniaoRequest("Grupo Centro editado", CategoriaReuniao.virtual, "descricao editada",
                "Terça-feira", "20h00", "link", null, null, null, null, null);
    }

    @Test
    void listarTodas_deveRetornarOkComListaDoService() {
        ReuniaoResponse item = reuniaoResponse(UUID.randomUUID());
        when(reuniaoService.listarTodas()).thenReturn(List.of(item));

        ResponseEntity<ApiResponse<List<ReuniaoResponse>>> resposta = reuniaoAdminController.listarTodas();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).containsExactly(item);
    }

    @Test
    void criar_deveRetornarCreatedComRespostaDoService() {
        CriarReuniaoRequest request = criarRequest();
        ReuniaoResponse esperado = reuniaoResponse(UUID.randomUUID());
        when(reuniaoService.criar(request)).thenReturn(esperado);

        ResponseEntity<ApiResponse<ReuniaoResponse>> resposta = reuniaoAdminController.criar(request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(reuniaoService).criar(request);
    }

    @Test
    void editar_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EditarReuniaoRequest request = editarRequest();
        ReuniaoResponse esperado = reuniaoResponse(id);
        when(reuniaoService.editar(id, request)).thenReturn(esperado);

        ResponseEntity<ApiResponse<ReuniaoResponse>> resposta = reuniaoAdminController.editar(id, request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(reuniaoService).editar(id, request);
    }

    @Test
    void remover_deveRetornarOkEChamarServiceComId() {
        UUID id = UUID.randomUUID();

        ResponseEntity<ApiResponse<MensagemResponse>> resposta = reuniaoAdminController.remover(id);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(reuniaoService).remover(id);
    }
}

package br.com.jogadoresanonimos.hotline.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.hotline.dto.*;
import br.com.jogadoresanonimos.hotline.service.HotlineService;
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
class HotlineAdminControllerTest {

    @Mock
    HotlineService hotlineService;

    HotlineAdminController hotlineAdminController;

    @BeforeEach
    void setUp() {
        hotlineAdminController = new HotlineAdminController(hotlineService);
    }

    private HotlineRegiaoResponse regiaoResponse(UUID id) {
        return new HotlineRegiaoResponse(id, "BA", "Salvador",
                List.of(new HotlineTelefoneResponse(UUID.randomUUID(), "(71) 0000-0000")));
    }

    @Test
    void listarTodas_deveRetornarOkComListaDoService() {
        HotlineRegiaoResponse item = regiaoResponse(UUID.randomUUID());
        when(hotlineService.listarTodas()).thenReturn(List.of(item));
        ResponseEntity<ApiResponse<List<HotlineRegiaoResponse>>> resposta = hotlineAdminController.listarTodas();
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).containsExactly(item);
    }

    @Test
    void criarRegiao_deveRetornarCreatedComRespostaDoService() {
        CriarRegiaoRequest request = new CriarRegiaoRequest("BA", "Salvador");
        HotlineRegiaoResponse esperado = regiaoResponse(UUID.randomUUID());
        when(hotlineService.criarRegiao(request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<HotlineRegiaoResponse>> resposta = hotlineAdminController.criarRegiao(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(hotlineService).criarRegiao(request);
    }

    @Test
    void editarRegiao_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EditarRegiaoRequest request = new EditarRegiaoRequest("RJ", "Niterói");
        HotlineRegiaoResponse esperado = regiaoResponse(id);
        when(hotlineService.editarRegiao(id, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<HotlineRegiaoResponse>> resposta = hotlineAdminController.editarRegiao(id, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(hotlineService).editarRegiao(id, request);
    }

    @Test
    void removerRegiao_deveRetornarOkEChamarServiceComId() {
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = hotlineAdminController.removerRegiao(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(hotlineService).removerRegiao(id);
    }

    @Test
    void adicionarTelefone_deveRetornarCreatedComRespostaDoService() {
        UUID regiaoId = UUID.randomUUID();
        CriarTelefoneRequest request = new CriarTelefoneRequest("(71) 0000-0000");
        HotlineRegiaoResponse esperado = regiaoResponse(regiaoId);
        when(hotlineService.adicionarTelefone(regiaoId, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<HotlineRegiaoResponse>> resposta =
                hotlineAdminController.adicionarTelefone(regiaoId, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(hotlineService).adicionarTelefone(regiaoId, request);
    }

    @Test
    void editarTelefone_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EditarTelefoneRequest request = new EditarTelefoneRequest("(71) 1111-1111");
        HotlineTelefoneResponse esperado = new HotlineTelefoneResponse(id, "(71) 1111-1111");
        when(hotlineService.editarTelefone(id, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<HotlineTelefoneResponse>> resposta =
                hotlineAdminController.editarTelefone(id, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(hotlineService).editarTelefone(id, request);
    }

    @Test
    void removerTelefone_deveRetornarOkEChamarServiceComId() {
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = hotlineAdminController.removerTelefone(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(hotlineService).removerTelefone(id);
    }
}

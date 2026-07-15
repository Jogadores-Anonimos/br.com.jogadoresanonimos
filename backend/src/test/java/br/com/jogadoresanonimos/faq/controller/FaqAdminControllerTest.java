package br.com.jogadoresanonimos.faq.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.faq.dto.CriarFaqRequest;
import br.com.jogadoresanonimos.faq.dto.EditarFaqRequest;
import br.com.jogadoresanonimos.faq.dto.FaqResponse;
import br.com.jogadoresanonimos.faq.service.FaqService;
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
class FaqAdminControllerTest {

    @Mock
    FaqService faqService;

    FaqAdminController faqAdminController;

    @BeforeEach
    void setUp() {
        faqAdminController = new FaqAdminController(faqService);
    }

    private FaqResponse faqResponse(UUID id) {
        return new FaqResponse(id, "pergunta", "resposta", 1);
    }

    @Test
    void listarTodos_deveRetornarOkComListaDoService() {
        FaqResponse item = faqResponse(UUID.randomUUID());
        when(faqService.listarTodos()).thenReturn(List.of(item));
        ResponseEntity<ApiResponse<List<FaqResponse>>> resposta = faqAdminController.listarTodos();
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).containsExactly(item);
    }

    @Test
    void criar_deveRetornarCreatedComRespostaDoServiceSemAceitarOrdem() {
        CriarFaqRequest request = new CriarFaqRequest("pergunta", "resposta");
        FaqResponse esperado = faqResponse(UUID.randomUUID());
        when(faqService.criar(request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<FaqResponse>> resposta = faqAdminController.criar(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(faqService).criar(request);
    }

    @Test
    void editar_deveRetornarOkComRespostaDoServiceSemAceitarOrdem() {
        UUID id = UUID.randomUUID();
        EditarFaqRequest request = new EditarFaqRequest("pergunta editada", "resposta editada");
        FaqResponse esperado = faqResponse(id);
        when(faqService.editar(id, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<FaqResponse>> resposta = faqAdminController.editar(id, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(faqService).editar(id, request);
    }

    @Test
    void remover_deveRetornarOkEChamarServiceComId() {
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = faqAdminController.remover(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(faqService).remover(id);
    }

    @Test
    void moverParaCima_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        FaqResponse esperado = faqResponse(id);
        when(faqService.moverParaCima(id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<FaqResponse>> resposta = faqAdminController.moverParaCima(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(faqService).moverParaCima(id);
    }

    @Test
    void moverParaBaixo_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        FaqResponse esperado = faqResponse(id);
        when(faqService.moverParaBaixo(id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<FaqResponse>> resposta = faqAdminController.moverParaBaixo(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(faqService).moverParaBaixo(id);
    }
}

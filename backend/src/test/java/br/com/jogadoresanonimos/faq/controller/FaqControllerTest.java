package br.com.jogadoresanonimos.faq.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaqControllerTest {

    @Mock
    FaqService faqService;

    FaqController faqController;

    @BeforeEach
    void setUp() {
        faqController = new FaqController(faqService);
    }

    @Test
    void listarTodos_deveRetornarOkComListaDoService() {
        FaqResponse item = new FaqResponse(UUID.randomUUID(), "pergunta", "resposta", 1);
        when(faqService.listarTodos()).thenReturn(List.of(item));

        ResponseEntity<ApiResponse<List<FaqResponse>>> resposta = faqController.listarTodos();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        assertThat(resposta.getBody().data()).containsExactly(item);
    }
}

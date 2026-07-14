package br.com.jogadoresanonimos.reflexao.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaResponse;
import br.com.jogadoresanonimos.reflexao.service.ReflexaoDiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReflexaoDiaControllerTest {

    @Mock
    ReflexaoDiaService reflexaoDiaService;

    ReflexaoDiaController reflexaoDiaController;

    @BeforeEach
    void setUp() {
        reflexaoDiaController = new ReflexaoDiaController(reflexaoDiaService);
    }

    @Test
    void buscarDeHoje_deveRetornarOkComRespostaDoService() {
        ReflexaoDiaResponse esperado = new ReflexaoDiaResponse(7, 12, "texto");
        when(reflexaoDiaService.buscarDeHoje()).thenReturn(esperado);

        ResponseEntity<ApiResponse<ReflexaoDiaResponse>> resposta = reflexaoDiaController.buscarDeHoje();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void buscarPorData_deveRetornarOkComRespostaDoService() {
        ReflexaoDiaResponse esperado = new ReflexaoDiaResponse(7, 12, "texto");
        when(reflexaoDiaService.buscarPorData(7, 12)).thenReturn(esperado);

        ResponseEntity<ApiResponse<ReflexaoDiaResponse>> resposta = reflexaoDiaController.buscarPorData(7, 12);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }
}

package br.com.jogadoresanonimos.reflexao.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaDoMesResponse;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaResponse;
import br.com.jogadoresanonimos.reflexao.dto.SalvarReflexaoDiaRequest;
import br.com.jogadoresanonimos.reflexao.service.ReflexaoDiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReflexaoDiaAdminControllerTest {

    @Mock
    ReflexaoDiaService reflexaoDiaService;

    ReflexaoDiaAdminController reflexaoDiaAdminController;

    @BeforeEach
    void setUp() {
        reflexaoDiaAdminController = new ReflexaoDiaAdminController(reflexaoDiaService);
    }

    @Test
    void listarMes_deveRetornarOkComListaDoService() {
        List<ReflexaoDiaDoMesResponse> esperado = List.of(new ReflexaoDiaDoMesResponse(1, "texto"),
                new ReflexaoDiaDoMesResponse(2, null));
        when(reflexaoDiaService.listarMes(7)).thenReturn(esperado);
        ResponseEntity<ApiResponse<List<ReflexaoDiaDoMesResponse>>> resposta = reflexaoDiaAdminController.listarMes(7);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void salvar_deveRetornarOkComRespostaDoServiceEChamarComMesEDia() {
        SalvarReflexaoDiaRequest request = new SalvarReflexaoDiaRequest("texto novo");
        ReflexaoDiaResponse esperado = new ReflexaoDiaResponse(7, 12, "texto novo");
        when(reflexaoDiaService.salvar(7, 12, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<ReflexaoDiaResponse>> resposta = reflexaoDiaAdminController.salvar(7, 12, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(reflexaoDiaService).salvar(7, 12, request);
    }
}

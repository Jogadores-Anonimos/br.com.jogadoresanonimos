package br.com.jogadoresanonimos.escritorio.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.escritorio.dto.EscritoriosRegionaisResponse;
import br.com.jogadoresanonimos.escritorio.service.EscritorioRegionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EscritorioRegionalControllerTest {

    @Mock
    EscritorioRegionalService escritorioRegionalService;

    EscritorioRegionalController controller;

    @BeforeEach
    void setUp() {
        controller = new EscritorioRegionalController(escritorioRegionalService);
    }

    @Test
    void listar_deveRetornarOkComEstruturaDoService() {
        EscritoriosRegionaisResponse esperado = new EscritoriosRegionaisResponse(List.of(), List.of());
        when(escritorioRegionalService.listarEstruturaCompleta()).thenReturn(esperado);

        ResponseEntity<ApiResponse<EscritoriosRegionaisResponse>> resposta = controller.listar();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }
}

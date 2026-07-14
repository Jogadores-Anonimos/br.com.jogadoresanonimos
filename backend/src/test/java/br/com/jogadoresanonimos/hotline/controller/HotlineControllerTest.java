package br.com.jogadoresanonimos.hotline.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.hotline.dto.HotlineRegiaoResponse;
import br.com.jogadoresanonimos.hotline.dto.HotlineTelefoneResponse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotlineControllerTest {

    @Mock
    HotlineService hotlineService;

    HotlineController hotlineController;

    @BeforeEach
    void setUp() {
        hotlineController = new HotlineController(hotlineService);
    }

    @Test
    void listarTodas_deveRetornarOkComListaDoService() {
        HotlineRegiaoResponse item = new HotlineRegiaoResponse(UUID.randomUUID(), "Bahia",
                List.of(new HotlineTelefoneResponse(UUID.randomUUID(), "Linha de apoio", "(71) 0000-0000")));
        when(hotlineService.listarTodas()).thenReturn(List.of(item));

        ResponseEntity<ApiResponse<List<HotlineRegiaoResponse>>> resposta = hotlineController.listarTodas();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        assertThat(resposta.getBody().data()).containsExactly(item);
    }
}

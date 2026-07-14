package br.com.jogadoresanonimos.mural.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.mural.dto.BannerMuralPublicoResponse;
import br.com.jogadoresanonimos.mural.service.BannerMuralService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MuralDeApoioControllerTest {

    @Mock
    BannerMuralService bannerMuralService;

    MuralDeApoioController muralDeApoioController;

    @BeforeEach
    void setUp() {
        muralDeApoioController = new MuralDeApoioController(bannerMuralService);
    }

    @Test
    void listarAtivos_deveRetornarOkComListaDoService() {
        BannerMuralPublicoResponse item = new BannerMuralPublicoResponse(UUID.randomUUID(), "https://storage/imagem.jpg",
                "alt", "legenda", "https://link", Instant.now());
        when(bannerMuralService.listarAtivos()).thenReturn(List.of(item));

        ResponseEntity<ApiResponse<List<BannerMuralPublicoResponse>>> resposta = muralDeApoioController.listarAtivos();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        assertThat(resposta.getBody().data()).containsExactly(item);
    }
}

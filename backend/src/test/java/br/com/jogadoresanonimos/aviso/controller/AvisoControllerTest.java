package br.com.jogadoresanonimos.aviso.controller;

import br.com.jogadoresanonimos.aviso.dto.AvisoPublicoResponse;
import br.com.jogadoresanonimos.aviso.service.AvisoService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
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
class AvisoControllerTest {

    @Mock
    AvisoService avisoService;

    AvisoController avisoController;

    @BeforeEach
    void setUp() {
        avisoController = new AvisoController(avisoService);
    }

    @Test
    void listarAtivos_deveRetornarOkComListaDoService() {
        AvisoPublicoResponse item = new AvisoPublicoResponse(UUID.randomUUID(), "titulo", "corpo", Instant.now(), true);
        when(avisoService.listarAtivos()).thenReturn(List.of(item));
        ResponseEntity<ApiResponse<List<AvisoPublicoResponse>>> resposta = avisoController.listarAtivos();
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        assertThat(resposta.getBody().data()).containsExactly(item);
    }
}

package br.com.jogadoresanonimos.reuniao.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoControllerTest {

    @Mock
    ReuniaoService reuniaoService;

    ReuniaoController reuniaoController;

    @BeforeEach
    void setUp() {
        reuniaoController = new ReuniaoController(reuniaoService);
    }

    @Test
    void listarTodas_deveRetornarOkComListaDoService() {
        ReuniaoResponse item = new ReuniaoResponse(UUID.randomUUID(), "Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "endereco", "5599999999999", "Segunda", "Noite",
                -23.5505, -46.6333);
        when(reuniaoService.listarTodas()).thenReturn(List.of(item));

        ResponseEntity<ApiResponse<List<ReuniaoResponse>>> resposta = reuniaoController.listarTodas();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        assertThat(resposta.getBody().data()).containsExactly(item);
    }
}

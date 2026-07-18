package br.com.jogadoresanonimos.mural.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.mural.dto.BannerMuralAdminResponse;
import br.com.jogadoresanonimos.mural.dto.CriarBannerMuralRequest;
import br.com.jogadoresanonimos.mural.dto.EditarBannerMuralRequest;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MuralDeApoioAdminControllerTest {

    @Mock
    BannerMuralService bannerMuralService;

    MuralDeApoioAdminController muralDeApoioAdminController;

    @BeforeEach
    void setUp() {
        muralDeApoioAdminController = new MuralDeApoioAdminController(bannerMuralService);
    }

    private BannerMuralAdminResponse bannerAdminResponse(UUID id, boolean ativo) {
        return new BannerMuralAdminResponse(id, UUID.randomUUID(), "https://storage/imagem.jpg", null, "alt",
                "legenda", "https://link", ativo, Instant.now(), Instant.now());
    }

    @Test
    void listarTodos_deveRetornarOkComListaDoService() {
        BannerMuralAdminResponse item = bannerAdminResponse(UUID.randomUUID(), false);
        when(bannerMuralService.listarTodos()).thenReturn(List.of(item));
        ResponseEntity<ApiResponse<List<BannerMuralAdminResponse>>> resposta = muralDeApoioAdminController.listarTodos();
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).containsExactly(item);
    }

    @Test
    void criar_deveRetornarCreatedComRespostaDoService() {
        CriarBannerMuralRequest request = new CriarBannerMuralRequest(UUID.randomUUID(), null, "alt", "legenda", "https://link");
        BannerMuralAdminResponse esperado = bannerAdminResponse(UUID.randomUUID(), true);
        when(bannerMuralService.criar(request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<BannerMuralAdminResponse>> resposta = muralDeApoioAdminController.criar(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(bannerMuralService).criar(request);
    }

    @Test
    void editar_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EditarBannerMuralRequest request = new EditarBannerMuralRequest(UUID.randomUUID(), null, "alt", "legenda", "https://link", false);
        BannerMuralAdminResponse esperado = bannerAdminResponse(id, false);
        when(bannerMuralService.editar(id, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<BannerMuralAdminResponse>> resposta = muralDeApoioAdminController.editar(id, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(bannerMuralService).editar(id, request);
    }

    @Test
    void remover_deveRetornarOkEChamarServiceComId() {
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = muralDeApoioAdminController.remover(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(bannerMuralService).remover(id);
    }
}

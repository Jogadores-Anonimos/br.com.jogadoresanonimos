package br.com.jogadoresanonimos.arquivo.controller;

import br.com.jogadoresanonimos.arquivo.dto.ArquivoResponse;
import br.com.jogadoresanonimos.arquivo.dto.EditarArquivoRequest;
import br.com.jogadoresanonimos.arquivo.service.ArquivoService;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArquivoControllerTest {

    @Mock
    ArquivoService arquivoService;

    @Mock
    StorageClient storageClient;

    ArquivoController arquivoController;

    @BeforeEach
    void setUp() {
        arquivoController = new ArquivoController(arquivoService, storageClient);
    }

    private ArquivoResponse arquivoResponse(UUID id) {
        return new ArquivoResponse(id, "livro.pdf", 1024L, "titulo", "descricao",
                "/api/v1/arquivos/" + id + "/download", null, Instant.now());
    }

    @Test
    void editar_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EditarArquivoRequest request = new EditarArquivoRequest("novo titulo", "nova descricao");
        ArquivoResponse esperado = arquivoResponse(id);
        when(arquivoService.editar(id, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<ArquivoResponse>> resposta = arquivoController.editar(id, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(arquivoService).editar(id, request);
    }

    @Test
    void remover_deveRetornarOkEChamarServiceComId() {
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = arquivoController.remover(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().success()).isTrue();
        verify(arquivoService).remover(id);
    }
}

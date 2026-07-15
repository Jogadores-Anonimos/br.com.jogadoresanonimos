package br.com.jogadoresanonimos.arquivo.service;

import br.com.jogadoresanonimos.arquivo.dto.ArquivoResponse;
import br.com.jogadoresanonimos.arquivo.dto.EditarArquivoRequest;
import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.cms.repository.CmsBlocoRepository;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import br.com.jogadoresanonimos.usuario.biblioteca.repository.UsuarioArquivoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArquivoServiceTest {

    @Mock
    ArquivoRepository arquivoRepository;

    @Mock
    CmsBlocoRepository cmsBlocoRepository;

    @Mock
    UsuarioArquivoRepository usuarioArquivoRepository;

    @Mock
    ArquivoProcessor arquivoProcessor;

    @Mock
    StorageClient storageClient;

    @Mock
    RateLimiterService rateLimiterService;

    ArquivoService arquivoService;

    @BeforeEach
    void setUp() {
        arquivoService = new ArquivoService(arquivoRepository, cmsBlocoRepository, usuarioArquivoRepository,
                arquivoProcessor, storageClient, rateLimiterService);
    }

    @Test
    void editar_deveAtualizarTituloEDescricaoSemTocarNoBinario() {
        UUID id = UUID.randomUUID();
        Arquivo arquivo = new Arquivo("livro.pdf", "chave.pdf", 1024L, "antigo", "descricao antiga", UUID.randomUUID());
        when(arquivoRepository.findById(id)).thenReturn(Optional.of(arquivo));
        EditarArquivoRequest request = new EditarArquivoRequest("  novo titulo  ", "  nova descricao  ");
        ArquivoResponse resultado = arquivoService.editar(id, request);
        assertThat(resultado.titulo()).isEqualTo("novo titulo");
        assertThat(resultado.descricao()).isEqualTo("nova descricao");
        assertThat(arquivo.getChaveArquivo()).isEqualTo("chave.pdf");
    }

    @Test
    void editar_deveLancarExcecaoQuandoArquivoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(arquivoRepository.findById(id)).thenReturn(Optional.empty());
        EditarArquivoRequest request = new EditarArquivoRequest("titulo", "descricao");
        assertThatThrownBy(() -> arquivoService.editar(id, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void editar_deveLancarExcecaoQuandoTituloEmBranco() {
        UUID id = UUID.randomUUID();
        Arquivo arquivo = new Arquivo("livro.pdf", "chave.pdf", 1024L, "antigo", "descricao antiga", UUID.randomUUID());
        when(arquivoRepository.findById(id)).thenReturn(Optional.of(arquivo));
        EditarArquivoRequest request = new EditarArquivoRequest("   ", "descricao");
        assertThatThrownBy(() -> arquivoService.editar(id, request))
                .isInstanceOf(ValidationException.class);
    }
}

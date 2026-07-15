package br.com.jogadoresanonimos.usuario.biblioteca;

import br.com.jogadoresanonimos.arquivo.dto.ArquivoResponse;
import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import br.com.jogadoresanonimos.usuario.biblioteca.repository.UsuarioArquivoRepository;
import br.com.jogadoresanonimos.usuario.biblioteca.service.BibliotecaPessoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BibliotecaPessoalServiceTest {

    @Mock
    UsuarioArquivoRepository usuarioArquivoRepository;

    @Mock
    ArquivoRepository arquivoRepository;

    @Mock
    StorageClient storageClient;

    @Mock
    RateLimiterService rateLimiterService;

    BibliotecaPessoalService service;

    @BeforeEach
    void setUp() {
        service = new BibliotecaPessoalService(usuarioArquivoRepository, arquivoRepository, storageClient,
                rateLimiterService);
    }

    @Test
    void listar_devePassarUsuarioIdParaORepositoryEMapearParaResponse() {
        UUID usuarioId = UUID.randomUUID();
        Arquivo arquivo = new Arquivo("livro.pdf", "chave", 100, "Título", "desc", UUID.randomUUID());
        when(usuarioArquivoRepository.listarArquivosDoUsuario(usuarioId)).thenReturn(List.of(arquivo));
        List<ArquivoResponse> resultado = service.listar(usuarioId);
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).titulo()).isEqualTo("Título");
        verify(usuarioArquivoRepository).listarArquivosDoUsuario(usuarioId);
    }

    @Test
    void adicionar_quandoJaExiste_naoDuplicaEIgnora() {
        UUID usuarioId = UUID.randomUUID();
        UUID arquivoId = UUID.randomUUID();
        when(usuarioArquivoRepository.existsById_UsuarioIdAndId_ArquivoId(usuarioId, arquivoId)).thenReturn(true);
        service.adicionar(usuarioId, arquivoId);
        verify(usuarioArquivoRepository, never()).save(any());
        verifyNoInteractions(arquivoRepository);
    }

    @Test
    void adicionar_quandoArquivoNaoExiste_lancaExcecao() {
        UUID usuarioId = UUID.randomUUID();
        UUID arquivoId = UUID.randomUUID();
        when(usuarioArquivoRepository.existsById_UsuarioIdAndId_ArquivoId(usuarioId, arquivoId)).thenReturn(false);
        when(arquivoRepository.existsById(arquivoId)).thenReturn(false);
        assertThatThrownBy(() -> service.adicionar(usuarioId, arquivoId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(usuarioArquivoRepository, never()).save(any());
    }

    @Test
    void adicionar_quandoNaoExisteAssociacaoEArquivoValido_salva() {
        UUID usuarioId = UUID.randomUUID();
        UUID arquivoId = UUID.randomUUID();
        when(usuarioArquivoRepository.existsById_UsuarioIdAndId_ArquivoId(usuarioId, arquivoId)).thenReturn(false);
        when(arquivoRepository.existsById(arquivoId)).thenReturn(true);
        service.adicionar(usuarioId, arquivoId);
        verify(usuarioArquivoRepository).save(any());
    }

    @Test
    void remover_devePassarUsuarioIdEArquivoIdParaORepository_semLancarErroSeNaoExistir() {
        UUID usuarioId = UUID.randomUUID();
        UUID arquivoId = UUID.randomUUID();
        service.remover(usuarioId, arquivoId);
        verify(usuarioArquivoRepository).deleteById_UsuarioIdAndId_ArquivoId(usuarioId, arquivoId);
    }
}

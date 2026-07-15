package br.com.jogadoresanonimos.cms.service;

import br.com.jogadoresanonimos.cms.dto.*;
import br.com.jogadoresanonimos.cms.entity.CmsPagina;
import br.com.jogadoresanonimos.cms.repository.CmsAlteracaoLogRepository;
import br.com.jogadoresanonimos.cms.repository.CmsPaginaRepository;
import br.com.jogadoresanonimos.cms.util.HtmlSanitizer;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CmsServiceTest {

    @Mock
    CmsPaginaRepository cmsPaginaRepository;
    @Mock
    CmsAlteracaoLogRepository cmsAlteracaoLogRepository;
    @Mock
    ImagemRepository imagemRepository;
    @Mock
    ArquivoRepository arquivoRepository;
    @Mock
    StorageClient storageClient;

    CmsService cmsService;

    @BeforeEach
    void setUp() {
        cmsService = new CmsService(cmsPaginaRepository, cmsAlteracaoLogRepository, imagemRepository,
                arquivoRepository, storageClient, new HtmlSanitizer(), new ObjectMapper());
    }

    @Test
    void listarAdmin_deveExcluirPaginasOcultasDaListagem() {
        when(cmsPaginaRepository.findByOcultaNaListagemAdminFalse())
                .thenReturn(List.of(new CmsPagina("sobre", "Sobre", UUID.randomUUID())));
        List<CmsPaginaListItemResponse> resultado = cmsService.listarAdmin();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).slug()).isEqualTo("sobre");
        verify(cmsPaginaRepository, never()).findAll();
    }

    @Test
    void buscarPublica_deveLancarNotFoundQuandoSlugNaoExiste() {
        when(cmsPaginaRepository.findBySlug("inexistente")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cmsService.buscarPublica("inexistente"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void criar_deveFalharComConflitoQuandoSlugJaExiste() {
        CmsPaginaCreateRequest request = new CmsPaginaCreateRequest("sobre-nos", "Sobre Nós",
                List.of(new BlocoRequest(1, "texto", "<p>oi</p>", null, null, null)));
        when(cmsPaginaRepository.existsBySlug("sobre-nos")).thenReturn(true);
        assertThatThrownBy(() -> cmsService.criar(request, UUID.randomUUID()))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void criar_deveFalharQuandoOrdemDuplicada() {
        CmsPaginaCreateRequest request = new CmsPaginaCreateRequest("sobre-nos", "Sobre Nós",
                List.of(
                        new BlocoRequest(1, "texto", "<p>a</p>", null, null, null),
                        new BlocoRequest(1, "texto", "<p>b</p>", null, null, null)
                ));
        when(cmsPaginaRepository.existsBySlug("sobre-nos")).thenReturn(false);
        assertThatThrownBy(() -> cmsService.criar(request, UUID.randomUUID()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void criar_deveFalharQuandoImagemIdNaoExiste() {
        UUID imagemIdInexistente = UUID.randomUUID();
        CmsPaginaCreateRequest request = new CmsPaginaCreateRequest("sobre-nos", "Sobre Nós",
                List.of(new BlocoRequest(1, "imagem", null, imagemIdInexistente, "descrição", null)));
        when(cmsPaginaRepository.existsBySlug("sobre-nos")).thenReturn(false);
        when(imagemRepository.existsById(imagemIdInexistente)).thenReturn(false);
        assertThatThrownBy(() -> cmsService.criar(request, UUID.randomUUID()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void criar_deveSanitizarConteudoDeTextoAntesDePersistir() {
        CmsPaginaCreateRequest request = new CmsPaginaCreateRequest("sobre-nos", "Sobre Nós",
                List.of(new BlocoRequest(1, "texto", "<script>alert(1)</script>", null, null, null)));
        when(cmsPaginaRepository.existsBySlug("sobre-nos")).thenReturn(false);
        assertThatThrownBy(() -> cmsService.criar(request, UUID.randomUUID()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void criar_devePersistirPaginaComBlocoDeTextoValido() {
        CmsPaginaCreateRequest request = new CmsPaginaCreateRequest("sobre-nos", "Sobre Nós",
                List.of(new BlocoRequest(1, "texto", "<p>conteúdo válido</p>", null, null, null)));
        when(cmsPaginaRepository.existsBySlug("sobre-nos")).thenReturn(false);
        CmsPagina[] capturada = new CmsPagina[1];
        when(cmsPaginaRepository.save(any(CmsPagina.class))).thenAnswer(inv -> {
            capturada[0] = inv.getArgument(0);
            return capturada[0];
        });
        when(cmsPaginaRepository.findBySlug("sobre-nos")).thenAnswer(inv -> Optional.ofNullable(capturada[0]));
        CmsPaginaAdminResponse response = cmsService.criar(request, UUID.randomUUID());
        assertThat(response.slug()).isEqualTo("sobre-nos");
        assertThat(response.blocos()).hasSize(1);
        assertThat(response.blocos().get(0).conteudo()).isEqualTo("<p>conteúdo válido</p>");
    }
}

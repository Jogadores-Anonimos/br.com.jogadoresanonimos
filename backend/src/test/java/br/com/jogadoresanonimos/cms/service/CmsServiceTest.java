package br.com.jogadoresanonimos.cms.service;

import br.com.jogadoresanonimos.cms.repository.CmsPaginaRepository;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CmsServiceTest {

    @Mock
    CmsPaginaRepository cmsPaginaRepository;
    @Mock
    ImagemRepository imagemRepository;
    @Mock
    ArquivoRepository arquivoRepository;
    @Mock
    StorageClient storageClient;

    CmsService cmsService;

    @BeforeEach
    void setUp() {
        cmsService = new CmsService(cmsPaginaRepository, imagemRepository, arquivoRepository, storageClient);
    }

    @Test
    void buscarPublica_deveLancarNotFoundQuandoSlugNaoExiste() {
        when(cmsPaginaRepository.findBySlug("inexistente")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cmsService.buscarPublica("inexistente"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}

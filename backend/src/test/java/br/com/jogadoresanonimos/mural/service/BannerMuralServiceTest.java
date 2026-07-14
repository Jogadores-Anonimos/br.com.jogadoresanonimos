package br.com.jogadoresanonimos.mural.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.imagem.entity.Imagem;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import br.com.jogadoresanonimos.mural.dto.BannerMuralAdminResponse;
import br.com.jogadoresanonimos.mural.dto.CriarBannerMuralRequest;
import br.com.jogadoresanonimos.mural.dto.EditarBannerMuralRequest;
import br.com.jogadoresanonimos.mural.entity.BannerMural;
import br.com.jogadoresanonimos.mural.repository.BannerMuralRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BannerMuralServiceTest {

    @Mock
    BannerMuralRepository bannerMuralRepository;
    @Mock
    ImagemRepository imagemRepository;
    @Mock
    StorageClient storageClient;

    BannerMuralService bannerMuralService;

    @BeforeEach
    void setUp() {
        bannerMuralService = new BannerMuralService(bannerMuralRepository, imagemRepository, storageClient);
    }

    private Imagem imagem(UUID id) {
        Imagem imagem = new Imagem("original.jpg", "chave-" + id, 1024, "alt", UUID.randomUUID());
        return imagem;
    }

    @Test
    void criar_deveFalharQuandoImagemNaoExiste() {
        UUID imagemId = UUID.randomUUID();
        CriarBannerMuralRequest request = new CriarBannerMuralRequest(imagemId, "alt", "legenda", "https://link");
        when(imagemRepository.existsById(imagemId)).thenReturn(false);

        assertThatThrownBy(() -> bannerMuralService.criar(request))
                .isInstanceOf(ValidationException.class);

        verify(bannerMuralRepository, never()).save(any());
    }

    @Test
    void criar_devePersistirEBannerAtivoPorPadrao() {
        UUID imagemId = UUID.randomUUID();
        CriarBannerMuralRequest request = new CriarBannerMuralRequest(imagemId, "alt", "legenda", "https://link");
        when(imagemRepository.existsById(imagemId)).thenReturn(true);
        when(bannerMuralRepository.save(any(BannerMural.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(imagemRepository.findById(imagemId)).thenReturn(Optional.empty());

        BannerMuralAdminResponse resposta = bannerMuralService.criar(request);

        ArgumentCaptor<BannerMural> captor = ArgumentCaptor.forClass(BannerMural.class);
        verify(bannerMuralRepository).save(captor.capture());
        assertThat(captor.getValue().isAtivo()).isTrue();
        assertThat(resposta.ativo()).isTrue();
        assertThat(resposta.altText()).isEqualTo("alt");
    }

    @Test
    void editar_deveLancarNotFoundQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        EditarBannerMuralRequest request = new EditarBannerMuralRequest(UUID.randomUUID(), "alt", "legenda", "https://link", false);
        when(bannerMuralRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bannerMuralService.editar(id, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void editar_devePermitirDesativarBanner() {
        UUID id = UUID.randomUUID();
        UUID imagemId = UUID.randomUUID();
        BannerMural banner = new BannerMural(imagemId, "alt antigo", "legenda antiga", null);
        EditarBannerMuralRequest request = new EditarBannerMuralRequest(imagemId, "alt novo", "legenda nova", "https://link", false);
        when(bannerMuralRepository.findById(id)).thenReturn(Optional.of(banner));
        when(imagemRepository.existsById(imagemId)).thenReturn(true);
        when(imagemRepository.findById(imagemId)).thenReturn(Optional.empty());

        BannerMuralAdminResponse resposta = bannerMuralService.editar(id, request);

        assertThat(resposta.ativo()).isFalse();
        assertThat(resposta.altText()).isEqualTo("alt novo");
    }

    @Test
    void listarAtivos_deveResolverUrlPublicaDaImagem() {
        UUID imagemId = UUID.randomUUID();
        BannerMural banner = new BannerMural(imagemId, "alt", "legenda", null);
        Imagem imagem = imagem(imagemId);
        when(bannerMuralRepository.findByAtivoTrue(any())).thenReturn(List.of(banner));
        when(imagemRepository.findById(imagemId)).thenReturn(Optional.of(imagem));
        when(storageClient.urlPublicaPara(imagem.getChaveArquivo())).thenReturn("https://storage/publica.jpg");

        var resultado = bannerMuralService.listarAtivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).imagemUrl()).isEqualTo("https://storage/publica.jpg");
    }

    @Test
    void remover_deveLancarNotFoundQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(bannerMuralRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bannerMuralService.remover(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(bannerMuralRepository, never()).delete(any());
    }
}

package br.com.jogadoresanonimos.hotline.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeRegiaoHotlineException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.hotline.dto.*;
import br.com.jogadoresanonimos.hotline.entity.HotlineRegiao;
import br.com.jogadoresanonimos.hotline.entity.HotlineTelefone;
import br.com.jogadoresanonimos.hotline.repository.HotlineRegiaoRepository;
import br.com.jogadoresanonimos.hotline.repository.HotlineTelefoneRepository;
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
class HotlineServiceTest {

    @Mock
    HotlineRegiaoRepository hotlineRegiaoRepository;

    @Mock
    HotlineTelefoneRepository hotlineTelefoneRepository;

    HotlineService hotlineService;

    @BeforeEach
    void setUp() {
        hotlineService = new HotlineService(hotlineRegiaoRepository, hotlineTelefoneRepository);
    }

    @Test
    void listarTodas_deveDelegarParaRepositoryEMapear() {
        HotlineRegiao regiao = new HotlineRegiao(UnidadeFederativa.BA, "Salvador");
        when(hotlineRegiaoRepository.findAllByOrderByUfAscCidadeAsc()).thenReturn(List.of(regiao));
        List<HotlineRegiaoResponse> resultado = hotlineService.listarTodas();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).uf()).isEqualTo("BA");
        assertThat(resultado.get(0).cidade()).isEqualTo("Salvador");
    }

    @Test
    void listarPublicas_deveRetornarApenasRegioesComTelefone() {
        HotlineRegiao regiao = new HotlineRegiao(UnidadeFederativa.BA, "Salvador");
        when(hotlineRegiaoRepository.findAllComPeloMenosUmTelefoneOrderByUfAscCidadeAsc()).thenReturn(List.of(regiao));
        List<HotlineRegiaoResponse> resultado = hotlineService.listarPublicas();
        assertThat(resultado).hasSize(1);
        verify(hotlineRegiaoRepository).findAllComPeloMenosUmTelefoneOrderByUfAscCidadeAsc();
    }

    @Test
    void criarRegiao_deveSalvarComUfECidadeTrimadaQuandoNaoExiste() {
        CriarRegiaoRequest request = new CriarRegiaoRequest("ba", "  Salvador  ");
        when(hotlineRegiaoRepository.existsByUfAndCidadeIgnoreCase(UnidadeFederativa.BA, "Salvador")).thenReturn(false);
        when(hotlineRegiaoRepository.save(any(HotlineRegiao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        HotlineRegiaoResponse resultado = hotlineService.criarRegiao(request);
        ArgumentCaptor<HotlineRegiao> captor = ArgumentCaptor.forClass(HotlineRegiao.class);
        verify(hotlineRegiaoRepository).save(captor.capture());
        assertThat(captor.getValue().getUf()).isEqualTo(UnidadeFederativa.BA);
        assertThat(captor.getValue().getCidade()).isEqualTo("Salvador");
        assertThat(resultado.uf()).isEqualTo("BA");
        assertThat(resultado.cidade()).isEqualTo("Salvador");
    }

    @Test
    void criarRegiao_deveLancarConflitoQuandoUfECidadeJaExistem() {
        CriarRegiaoRequest request = new CriarRegiaoRequest("BA", "Salvador");
        when(hotlineRegiaoRepository.existsByUfAndCidadeIgnoreCase(UnidadeFederativa.BA, "Salvador")).thenReturn(true);
        assertThatThrownBy(() -> hotlineService.criarRegiao(request))
                .isInstanceOf(ConflitoDeRegiaoHotlineException.class);
        verify(hotlineRegiaoRepository, never()).save(any());
    }

    @Test
    void criarRegiao_deveLancarValidationExceptionQuandoUfInvalida() {
        CriarRegiaoRequest request = new CriarRegiaoRequest("XX", "Salvador");
        assertThatThrownBy(() -> hotlineService.criarRegiao(request))
                .isInstanceOf(ValidationException.class);
        verify(hotlineRegiaoRepository, never()).save(any());
    }

    @Test
    void editarRegiao_deveAtualizarQuandoNaoHaConflito() {
        UUID id = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao(UnidadeFederativa.BA, "Salvador");
        when(hotlineRegiaoRepository.findById(id)).thenReturn(Optional.of(regiao));
        when(hotlineRegiaoRepository.existsByUfAndCidadeIgnoreCase(UnidadeFederativa.RJ, "Niterói")).thenReturn(false);
        EditarRegiaoRequest request = new EditarRegiaoRequest("RJ", "Niterói");
        HotlineRegiaoResponse resultado = hotlineService.editarRegiao(id, request);
        assertThat(resultado.uf()).isEqualTo("RJ");
        assertThat(resultado.cidade()).isEqualTo("Niterói");
    }

    @Test
    void editarRegiao_deveLancarConflitoQuandoNovaChaveJaExisteEmOutraRegiao() {
        UUID id = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao(UnidadeFederativa.BA, "Salvador");
        when(hotlineRegiaoRepository.findById(id)).thenReturn(Optional.of(regiao));
        when(hotlineRegiaoRepository.existsByUfAndCidadeIgnoreCase(UnidadeFederativa.RJ, "Niterói")).thenReturn(true);
        EditarRegiaoRequest request = new EditarRegiaoRequest("RJ", "Niterói");
        assertThatThrownBy(() -> hotlineService.editarRegiao(id, request))
                .isInstanceOf(ConflitoDeRegiaoHotlineException.class);
    }

    @Test
    void editarRegiao_naoDeveValidarConflitoQuandoChaveNaoMuda() {
        UUID id = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao(UnidadeFederativa.BA, "Salvador");
        when(hotlineRegiaoRepository.findById(id)).thenReturn(Optional.of(regiao));
        EditarRegiaoRequest request = new EditarRegiaoRequest("BA", "Salvador");
        hotlineService.editarRegiao(id, request);
        verify(hotlineRegiaoRepository, never()).existsByUfAndCidadeIgnoreCase(any(), any());
    }

    @Test
    void removerRegiao_deveDeletarQuandoExiste() {
        UUID id = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao(UnidadeFederativa.BA, "Salvador");
        when(hotlineRegiaoRepository.findById(id)).thenReturn(Optional.of(regiao));
        hotlineService.removerRegiao(id);
        verify(hotlineRegiaoRepository).delete(regiao);
    }

    @Test
    void removerRegiao_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(hotlineRegiaoRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> hotlineService.removerRegiao(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(hotlineRegiaoRepository, never()).delete(any());
    }

    @Test
    void adicionarTelefone_deveAdicionarNaRegiaoExistente() {
        UUID regiaoId = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao(UnidadeFederativa.BA, "Salvador");
        when(hotlineRegiaoRepository.findById(regiaoId)).thenReturn(Optional.of(regiao));
        when(hotlineTelefoneRepository.save(any(HotlineTelefone.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CriarTelefoneRequest request = new CriarTelefoneRequest("  (71) 0000-0000  ");
        HotlineRegiaoResponse resultado = hotlineService.adicionarTelefone(regiaoId, request);
        assertThat(regiao.getTelefones()).hasSize(1);
        assertThat(regiao.getTelefones().get(0).getTelefone()).isEqualTo("(71) 0000-0000");
        assertThat(resultado.telefones()).hasSize(1);
    }

    @Test
    void adicionarTelefone_deveLancarExcecaoQuandoRegiaoNaoExiste() {
        UUID regiaoId = UUID.randomUUID();
        when(hotlineRegiaoRepository.findById(regiaoId)).thenReturn(Optional.empty());
        CriarTelefoneRequest request = new CriarTelefoneRequest("(71) 0000-0000");
        assertThatThrownBy(() -> hotlineService.adicionarTelefone(regiaoId, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(hotlineTelefoneRepository, never()).save(any());
    }

    @Test
    void editarTelefone_deveAtualizarTelefoneExistente() {
        UUID id = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao(UnidadeFederativa.BA, "Salvador");
        HotlineTelefone telefone = new HotlineTelefone(regiao, "(71) 0000-0000");
        when(hotlineTelefoneRepository.findById(id)).thenReturn(Optional.of(telefone));
        EditarTelefoneRequest request = new EditarTelefoneRequest("(71) 1111-1111");
        HotlineTelefoneResponse resultado = hotlineService.editarTelefone(id, request);
        assertThat(resultado.telefone()).isEqualTo("(71) 1111-1111");
    }

    @Test
    void editarTelefone_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(hotlineTelefoneRepository.findById(id)).thenReturn(Optional.empty());
        EditarTelefoneRequest request = new EditarTelefoneRequest("(71) 1111-1111");
        assertThatThrownBy(() -> hotlineService.editarTelefone(id, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void removerTelefone_deveDeletarQuandoExiste() {
        UUID id = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao(UnidadeFederativa.BA, "Salvador");
        HotlineTelefone telefone = new HotlineTelefone(regiao, "(71) 0000-0000");
        when(hotlineTelefoneRepository.findById(id)).thenReturn(Optional.of(telefone));
        hotlineService.removerTelefone(id);
        verify(hotlineTelefoneRepository).delete(telefone);
    }

    @Test
    void removerTelefone_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(hotlineTelefoneRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> hotlineService.removerTelefone(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(hotlineTelefoneRepository, never()).delete(any());
    }
}

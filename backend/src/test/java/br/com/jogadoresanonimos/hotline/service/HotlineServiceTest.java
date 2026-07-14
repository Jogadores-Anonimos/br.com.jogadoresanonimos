package br.com.jogadoresanonimos.hotline.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
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
        HotlineRegiao regiao = new HotlineRegiao("Bahia");
        when(hotlineRegiaoRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(regiao));

        List<HotlineRegiaoResponse> resultado = hotlineService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Bahia");
    }

    @Test
    void criarRegiao_deveSalvarComNomeTrimadoQuandoNaoExiste() {
        CriarRegiaoRequest request = new CriarRegiaoRequest("  Bahia  ");
        when(hotlineRegiaoRepository.existsByNomeIgnoreCase("Bahia")).thenReturn(false);
        when(hotlineRegiaoRepository.save(any(HotlineRegiao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HotlineRegiaoResponse resultado = hotlineService.criarRegiao(request);

        ArgumentCaptor<HotlineRegiao> captor = ArgumentCaptor.forClass(HotlineRegiao.class);
        verify(hotlineRegiaoRepository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Bahia");
        assertThat(resultado.nome()).isEqualTo("Bahia");
    }

    @Test
    void criarRegiao_deveLancarConflitoQuandoNomeJaExiste() {
        CriarRegiaoRequest request = new CriarRegiaoRequest("Bahia");
        when(hotlineRegiaoRepository.existsByNomeIgnoreCase("Bahia")).thenReturn(true);

        assertThatThrownBy(() -> hotlineService.criarRegiao(request))
                .isInstanceOf(ConflitoDeEstadoException.class);

        verify(hotlineRegiaoRepository, never()).save(any());
    }

    @Test
    void removerRegiao_deveDeletarQuandoExiste() {
        UUID id = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao("Bahia");
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
        HotlineRegiao regiao = new HotlineRegiao("Bahia");
        when(hotlineRegiaoRepository.findById(regiaoId)).thenReturn(Optional.of(regiao));
        when(hotlineTelefoneRepository.save(any(HotlineTelefone.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CriarTelefoneRequest request = new CriarTelefoneRequest("  Linha de apoio  ", "  (71) 0000-0000  ");

        HotlineRegiaoResponse resultado = hotlineService.adicionarTelefone(regiaoId, request);

        assertThat(regiao.getTelefones()).hasSize(1);
        assertThat(regiao.getTelefones().get(0).getLabel()).isEqualTo("Linha de apoio");
        assertThat(regiao.getTelefones().get(0).getTelefone()).isEqualTo("(71) 0000-0000");
        assertThat(resultado.telefones()).hasSize(1);
    }

    @Test
    void adicionarTelefone_deveLancarExcecaoQuandoRegiaoNaoExiste() {
        UUID regiaoId = UUID.randomUUID();
        when(hotlineRegiaoRepository.findById(regiaoId)).thenReturn(Optional.empty());
        CriarTelefoneRequest request = new CriarTelefoneRequest("Linha de apoio", "(71) 0000-0000");

        assertThatThrownBy(() -> hotlineService.adicionarTelefone(regiaoId, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(hotlineTelefoneRepository, never()).save(any());
    }

    @Test
    void editarTelefone_deveAtualizarTelefoneExistente() {
        UUID id = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao("Bahia");
        HotlineTelefone telefone = new HotlineTelefone(regiao, "antigo", "(71) 0000-0000");
        when(hotlineTelefoneRepository.findById(id)).thenReturn(Optional.of(telefone));
        EditarTelefoneRequest request = new EditarTelefoneRequest("novo", "(71) 1111-1111");

        HotlineTelefoneResponse resultado = hotlineService.editarTelefone(id, request);

        assertThat(resultado.label()).isEqualTo("novo");
        assertThat(resultado.telefone()).isEqualTo("(71) 1111-1111");
    }

    @Test
    void editarTelefone_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(hotlineTelefoneRepository.findById(id)).thenReturn(Optional.empty());
        EditarTelefoneRequest request = new EditarTelefoneRequest("novo", "(71) 1111-1111");

        assertThatThrownBy(() -> hotlineService.editarTelefone(id, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void removerTelefone_deveDeletarQuandoExiste() {
        UUID id = UUID.randomUUID();
        HotlineRegiao regiao = new HotlineRegiao("Bahia");
        HotlineTelefone telefone = new HotlineTelefone(regiao, "Linha de apoio", "(71) 0000-0000");
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

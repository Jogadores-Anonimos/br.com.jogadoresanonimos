package br.com.jogadoresanonimos.vinteperguntas.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoCriarRequest;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoResponse;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoUpdateRequest;
import br.com.jogadoresanonimos.vinteperguntas.entity.VinteQuestao;
import br.com.jogadoresanonimos.vinteperguntas.repository.VinteQuestaoRepository;
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
class VinteQuestaoServiceTest {

    @Mock
    VinteQuestaoRepository vinteQuestaoRepository;

    VinteQuestaoService vinteQuestaoService;

    UUID adminId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        vinteQuestaoService = new VinteQuestaoService(vinteQuestaoRepository);
    }

    @Test
    void listar_deveDelegarParaRepositoryOrdenadoEDerivarNumero() {
        VinteQuestao q1 = new VinteQuestao(0, "primeira", adminId);
        VinteQuestao q2 = new VinteQuestao(1, "segunda", adminId);
        when(vinteQuestaoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(q1, q2));
        List<VinteQuestaoResponse> resultado = vinteQuestaoService.listar();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).numero()).isEqualTo(1);
        assertThat(resultado.get(1).numero()).isEqualTo(2);
    }

    @Test
    void criar_deveSalvarComTextoTrimadoEOrdemNoFinal() {
        VinteQuestaoCriarRequest request = new VinteQuestaoCriarRequest("  pergunta nova  ");
        VinteQuestao existente = new VinteQuestao(0, "existente", adminId);
        when(vinteQuestaoRepository.findAllByOrderByOrdemAsc())
                .thenReturn(List.of(existente))
                .thenReturn(List.of(existente, new VinteQuestao(1, "pergunta nova", adminId)));
        when(vinteQuestaoRepository.save(any(VinteQuestao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        VinteQuestaoResponse resultado = vinteQuestaoService.criar(request, adminId);
        ArgumentCaptor<VinteQuestao> captor = ArgumentCaptor.forClass(VinteQuestao.class);
        verify(vinteQuestaoRepository).save(captor.capture());
        assertThat(captor.getValue().getTexto()).isEqualTo("pergunta nova");
        assertThat(captor.getValue().getOrdem()).isEqualTo(1);
        assertThat(resultado.texto()).isEqualTo("pergunta nova");
    }

    @Test
    void criar_devePosicionarPrimeiraPerguntaComOrdemZeroQuandoTabelaVazia() {
        VinteQuestaoCriarRequest request = new VinteQuestaoCriarRequest("primeira pergunta");
        when(vinteQuestaoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of());
        when(vinteQuestaoRepository.save(any(VinteQuestao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        VinteQuestaoResponse resultado = vinteQuestaoService.criar(request, adminId);
        assertThat(resultado.ordem()).isEqualTo(0);
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(vinteQuestaoRepository.findById(id)).thenReturn(Optional.empty());
        VinteQuestaoUpdateRequest request = new VinteQuestaoUpdateRequest("novo texto");
        assertThatThrownBy(() -> vinteQuestaoService.atualizar(id, request, adminId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void remover_deveDeletarQuandoHaMaisDeUmaPergunta() {
        UUID id = UUID.randomUUID();
        VinteQuestao questao = new VinteQuestao(0, "pergunta", adminId);
        when(vinteQuestaoRepository.findById(id)).thenReturn(Optional.of(questao));
        when(vinteQuestaoRepository.count()).thenReturn(2L);
        vinteQuestaoService.remover(id);
        verify(vinteQuestaoRepository).delete(questao);
    }

    @Test
    void remover_deveBloquearRemocaoDaUltimaPergunta() {
        UUID id = UUID.randomUUID();
        VinteQuestao questao = new VinteQuestao(0, "pergunta", adminId);
        when(vinteQuestaoRepository.findById(id)).thenReturn(Optional.of(questao));
        when(vinteQuestaoRepository.count()).thenReturn(1L);
        assertThatThrownBy(() -> vinteQuestaoService.remover(id))
                .isInstanceOf(ValidationException.class);
        verify(vinteQuestaoRepository, never()).delete(any());
    }

    @Test
    void remover_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(vinteQuestaoRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vinteQuestaoService.remover(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(vinteQuestaoRepository, never()).delete(any());
    }

    @Test
    void moverParaCima_deveTrocarOrdemComVizinhoAnterior() {
        UUID id = UUID.randomUUID();
        VinteQuestao primeira = new VinteQuestao(0, "primeira", adminId);
        VinteQuestao segunda = new VinteQuestao(1, "segunda", adminId);
        setId(primeira, UUID.randomUUID());
        setId(segunda, id);
        when(vinteQuestaoRepository.findById(id)).thenReturn(Optional.of(segunda));
        when(vinteQuestaoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeira, segunda));
        when(vinteQuestaoRepository.buscarPorIdComLock(primeira.getId())).thenReturn(Optional.of(primeira));
        when(vinteQuestaoRepository.buscarPorIdComLock(segunda.getId())).thenReturn(Optional.of(segunda));
        VinteQuestaoResponse resultado = vinteQuestaoService.moverParaCima(id);
        assertThat(resultado.ordem()).isEqualTo(0);
        assertThat(primeira.getOrdem()).isEqualTo(1);
    }

    @Test
    void moverParaBaixo_deveTrocarOrdemComVizinhoPosterior() {
        UUID id = UUID.randomUUID();
        VinteQuestao primeira = new VinteQuestao(0, "primeira", adminId);
        VinteQuestao segunda = new VinteQuestao(1, "segunda", adminId);
        setId(primeira, id);
        setId(segunda, UUID.randomUUID());
        when(vinteQuestaoRepository.findById(id)).thenReturn(Optional.of(primeira));
        when(vinteQuestaoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeira, segunda));
        when(vinteQuestaoRepository.buscarPorIdComLock(primeira.getId())).thenReturn(Optional.of(primeira));
        when(vinteQuestaoRepository.buscarPorIdComLock(segunda.getId())).thenReturn(Optional.of(segunda));
        VinteQuestaoResponse resultado = vinteQuestaoService.moverParaBaixo(id);
        assertThat(resultado.ordem()).isEqualTo(1);
        assertThat(segunda.getOrdem()).isEqualTo(0);
    }

    private static void setId(Object entity, UUID id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}

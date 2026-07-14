package br.com.jogadoresanonimos.faq.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.faq.dto.CriarFaqRequest;
import br.com.jogadoresanonimos.faq.dto.EditarFaqRequest;
import br.com.jogadoresanonimos.faq.dto.FaqResponse;
import br.com.jogadoresanonimos.faq.entity.Faq;
import br.com.jogadoresanonimos.faq.repository.FaqRepository;
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
class FaqServiceTest {

    @Mock
    FaqRepository faqRepository;

    FaqService faqService;

    @BeforeEach
    void setUp() {
        faqService = new FaqService(faqRepository);
    }

    @Test
    void listarTodos_deveDelegarParaRepositoryOrdenadoEMapear() {
        Faq faq = new Faq("pergunta", "resposta", 1);
        when(faqRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(faq));

        List<FaqResponse> resultado = faqService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).pergunta()).isEqualTo("pergunta");
        assertThat(resultado.get(0).ordem()).isEqualTo(1);
    }

    @Test
    void criar_deveSalvarComTextoTrimadoENaoAceitarOrdemDoPayload() {
        CriarFaqRequest request = new CriarFaqRequest("  pergunta  ", "  resposta  ");
        when(faqRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(new Faq("existente", "resp", 0)));
        when(faqRepository.save(any(Faq.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FaqResponse resultado = faqService.criar(request);

        ArgumentCaptor<Faq> captor = ArgumentCaptor.forClass(Faq.class);
        verify(faqRepository).save(captor.capture());
        assertThat(captor.getValue().getPergunta()).isEqualTo("pergunta");
        assertThat(captor.getValue().getResposta()).isEqualTo("resposta");
        assertThat(resultado.ordem()).isEqualTo(1);
    }

    @Test
    void criar_devePosicionarPrimeiroItemComOrdemZeroQuandoListaVazia() {
        CriarFaqRequest request = new CriarFaqRequest("pergunta", "resposta");
        when(faqRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of());
        when(faqRepository.save(any(Faq.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FaqResponse resultado = faqService.criar(request);

        assertThat(resultado.ordem()).isEqualTo(0);
    }

    @Test
    void editar_deveAtualizarFaqExistenteSemAlterarOrdem() {
        UUID id = UUID.randomUUID();
        Faq faq = new Faq("antiga", "resposta antiga", 1);
        when(faqRepository.findById(id)).thenReturn(Optional.of(faq));
        EditarFaqRequest request = new EditarFaqRequest("nova", "resposta nova");

        FaqResponse resultado = faqService.editar(id, request);

        assertThat(resultado.pergunta()).isEqualTo("nova");
        assertThat(resultado.ordem()).isEqualTo(1);
    }

    @Test
    void editar_deveLancarExcecaoQuandoFaqNaoExiste() {
        UUID id = UUID.randomUUID();
        when(faqRepository.findById(id)).thenReturn(Optional.empty());
        EditarFaqRequest request = new EditarFaqRequest("nova", "resposta nova");

        assertThatThrownBy(() -> faqService.editar(id, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void remover_deveDeletarQuandoExiste() {
        UUID id = UUID.randomUUID();
        Faq faq = new Faq("pergunta", "resposta", 1);
        when(faqRepository.findById(id)).thenReturn(Optional.of(faq));

        faqService.remover(id);

        verify(faqRepository).delete(faq);
    }

    @Test
    void remover_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(faqRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> faqService.remover(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(faqRepository, never()).delete(any());
    }

    // ---- Reordenação ----

    @Test
    void moverParaCima_deveTrocarOrdemComVizinhoAnterior() {
        UUID id = UUID.randomUUID();
        Faq primeira = new Faq("primeira", "r1", 0);
        Faq segunda = new Faq("segunda", "r2", 1);
        setId(primeira, UUID.randomUUID());
        setId(segunda, id);
        when(faqRepository.findById(id)).thenReturn(Optional.of(segunda));
        when(faqRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeira, segunda));

        FaqResponse resultado = faqService.moverParaCima(id);

        assertThat(resultado.ordem()).isEqualTo(0);
        assertThat(primeira.getOrdem()).isEqualTo(1);
    }

    @Test
    void moverParaCima_deveSerNoOpQuandoJaEhPrimeiro() {
        UUID id = UUID.randomUUID();
        Faq primeira = new Faq("primeira", "r1", 0);
        setId(primeira, id);
        Faq segunda = new Faq("segunda", "r2", 1);
        when(faqRepository.findById(id)).thenReturn(Optional.of(primeira));
        when(faqRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeira, segunda));

        FaqResponse resultado = faqService.moverParaCima(id);

        assertThat(resultado.ordem()).isEqualTo(0);
        assertThat(segunda.getOrdem()).isEqualTo(1);
    }

    @Test
    void moverParaBaixo_deveTrocarOrdemComVizinhoPosterior() {
        UUID id = UUID.randomUUID();
        Faq primeira = new Faq("primeira", "r1", 0);
        Faq segunda = new Faq("segunda", "r2", 1);
        setId(primeira, id);
        setId(segunda, UUID.randomUUID());
        when(faqRepository.findById(id)).thenReturn(Optional.of(primeira));
        when(faqRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeira, segunda));

        FaqResponse resultado = faqService.moverParaBaixo(id);

        assertThat(resultado.ordem()).isEqualTo(1);
        assertThat(segunda.getOrdem()).isEqualTo(0);
    }

    @Test
    void moverParaBaixo_deveSerNoOpQuandoJaEhUltimo() {
        UUID id = UUID.randomUUID();
        Faq primeira = new Faq("primeira", "r1", 0);
        Faq segunda = new Faq("segunda", "r2", 1);
        setId(segunda, id);
        when(faqRepository.findById(id)).thenReturn(Optional.of(segunda));
        when(faqRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeira, segunda));

        FaqResponse resultado = faqService.moverParaBaixo(id);

        assertThat(resultado.ordem()).isEqualTo(1);
        assertThat(primeira.getOrdem()).isEqualTo(0);
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

package br.com.jogadoresanonimos.aviso.service;

import br.com.jogadoresanonimos.aviso.dto.AvisoAdminResponse;
import br.com.jogadoresanonimos.aviso.dto.CriarAvisoRequest;
import br.com.jogadoresanonimos.aviso.dto.EditarAvisoRequest;
import br.com.jogadoresanonimos.aviso.entity.Aviso;
import br.com.jogadoresanonimos.aviso.repository.AvisoRepository;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvisoServiceTest {

    @Mock
    AvisoRepository avisoRepository;

    AvisoService avisoService;

    @BeforeEach
    void setUp() {
        avisoService = new AvisoService(avisoRepository);
    }

    @Test
    void listarAtivos_deveDelegarParaRepositoryComInstanteAtualEMapear() {
        Aviso aviso = new Aviso("titulo", "corpo", true, null);
        when(avisoRepository.findAtivosOrdenados(any(Instant.class))).thenReturn(List.of(aviso));
        var resultado = avisoService.listarAtivos();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).titulo()).isEqualTo("titulo");
        assertThat(resultado.get(0).fixado()).isTrue();
    }

    @Test
    void listarTodos_deveRetornarTodosOrdenadosIncluindoExpirado() {
        Aviso expirado = new Aviso("expirado", "corpo", false, Instant.now().minus(1, ChronoUnit.DAYS));
        when(avisoRepository.findAllByOrderByFixadoDescCriadoEmDesc()).thenReturn(List.of(expirado));
        List<AvisoAdminResponse> resultado = avisoService.listarTodos();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).expirado()).isTrue();
    }

    @Test
    void criar_deveSalvarComTextoTrimadoERetornarResponse() {
        CriarAvisoRequest request = new CriarAvisoRequest("  titulo  ", "  corpo  ", false, null);
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AvisoAdminResponse resultado = avisoService.criar(request);
        ArgumentCaptor<Aviso> captor = ArgumentCaptor.forClass(Aviso.class);
        verify(avisoRepository).save(captor.capture());
        assertThat(captor.getValue().getTitulo()).isEqualTo("titulo");
        assertThat(captor.getValue().getCorpo()).isEqualTo("corpo");
        assertThat(resultado.titulo()).isEqualTo("titulo");
    }

    @Test
    void editar_deveAtualizarAvisoExistente() {
        UUID id = UUID.randomUUID();
        Aviso aviso = new Aviso("antigo", "corpo antigo", false, null);
        when(avisoRepository.findById(id)).thenReturn(Optional.of(aviso));
        EditarAvisoRequest request = new EditarAvisoRequest("novo", "corpo novo", true, null);
        AvisoAdminResponse resultado = avisoService.editar(id, request);
        assertThat(resultado.titulo()).isEqualTo("novo");
        assertThat(resultado.fixado()).isTrue();
    }

    @Test
    void editar_deveLancarExcecaoQuandoAvisoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(avisoRepository.findById(id)).thenReturn(Optional.empty());
        EditarAvisoRequest request = new EditarAvisoRequest("novo", "corpo novo", true, null);
        assertThatThrownBy(() -> avisoService.editar(id, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void remover_deveDeletarQuandoExiste() {
        UUID id = UUID.randomUUID();
        Aviso aviso = new Aviso("titulo", "corpo", false, null);
        when(avisoRepository.findById(id)).thenReturn(Optional.of(aviso));
        avisoService.remover(id);
        verify(avisoRepository).delete(aviso);
    }

    @Test
    void remover_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(avisoRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> avisoService.remover(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(avisoRepository, never()).delete(any());
    }
}

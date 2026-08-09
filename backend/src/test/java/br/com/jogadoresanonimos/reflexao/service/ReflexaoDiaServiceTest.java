package br.com.jogadoresanonimos.reflexao.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaDoMesResponse;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaResponse;
import br.com.jogadoresanonimos.reflexao.dto.SalvarReflexaoDiaRequest;
import br.com.jogadoresanonimos.reflexao.entity.ReflexaoDia;
import br.com.jogadoresanonimos.reflexao.repository.ReflexaoDiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReflexaoDiaServiceTest {

    @Mock
    ReflexaoDiaRepository reflexaoDiaRepository;

    ReflexaoDiaService reflexaoDiaService;

    @BeforeEach
    void setUp() {
        reflexaoDiaService = new ReflexaoDiaService(reflexaoDiaRepository);
    }

    @Test
    void buscarDeHoje_deveDelegarParaBuscarPorDataComMesEDiaAtuais() {
        LocalDate hoje = LocalDate.now();
        ReflexaoDia reflexaoDia = new ReflexaoDia(hoje.getMonthValue(), hoje.getDayOfMonth(), "texto de hoje");
        when(reflexaoDiaRepository.findByMesAndDia(hoje.getMonthValue(), hoje.getDayOfMonth()))
                .thenReturn(Optional.of(reflexaoDia));
        ReflexaoDiaResponse resultado = reflexaoDiaService.buscarDeHoje();
        assertThat(resultado.mes()).isEqualTo(hoje.getMonthValue());
        assertThat(resultado.dia()).isEqualTo(hoje.getDayOfMonth());
        assertThat(resultado.texto()).isEqualTo("texto de hoje");
    }

    @Test
    void buscarPorData_deveRetornarReflexaoQuandoExiste() {
        ReflexaoDia reflexaoDia = new ReflexaoDia(7, 12, "texto do dia");
        when(reflexaoDiaRepository.findByMesAndDia(7, 12)).thenReturn(Optional.of(reflexaoDia));
        ReflexaoDiaResponse resultado = reflexaoDiaService.buscarPorData(7, 12);
        assertThat(resultado.texto()).isEqualTo("texto do dia");
    }

    @Test
    void buscarPorData_deveLancarNotFoundQuandoNaoCadastrada() {
        when(reflexaoDiaRepository.findByMesAndDia(7, 12)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reflexaoDiaService.buscarPorData(7, 12))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void buscarPorData_deveLancarValidationExceptionParaMesInvalido() {
        assertThatThrownBy(() -> reflexaoDiaService.buscarPorData(13, 1))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(reflexaoDiaRepository);
    }

    @Test
    void buscarPorData_deveLancarValidationExceptionParaDiaInvalidoNoMes() {
        assertThatThrownBy(() -> reflexaoDiaService.buscarPorData(2, 30))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(reflexaoDiaRepository);
    }

    @Test
    void listarMes_deveRetornarTodosOsDiasDoMesComNullParaNaoCadastrados() {
        ReflexaoDia reflexaoDia = new ReflexaoDia(2, 5, "texto do dia 5");
        when(reflexaoDiaRepository.findAllByMesOrderByDiaAsc(2)).thenReturn(List.of(reflexaoDia));
        List<ReflexaoDiaDoMesResponse> resultado = reflexaoDiaService.listarMes(2);
        assertThat(resultado).hasSize(29);
        assertThat(resultado.get(4).dia()).isEqualTo(5);
        assertThat(resultado.get(4).texto()).isEqualTo("texto do dia 5");
        assertThat(resultado.get(0).texto()).isNull();
    }

    @Test
    void listarMes_deveLancarValidationExceptionParaMesInvalido() {
        assertThatThrownBy(() -> reflexaoDiaService.listarMes(0))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(reflexaoDiaRepository);
    }

    @Test
    void salvar_deveCriarNovaReflexaoQuandoNaoExiste() {
        when(reflexaoDiaRepository.findByMesAndDia(7, 12)).thenReturn(Optional.empty());
        when(reflexaoDiaRepository.save(any(ReflexaoDia.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SalvarReflexaoDiaRequest request = new SalvarReflexaoDiaRequest("  texto novo  ");
        ReflexaoDiaResponse resultado = reflexaoDiaService.salvar(7, 12, request);
        ArgumentCaptor<ReflexaoDia> captor = ArgumentCaptor.forClass(ReflexaoDia.class);
        verify(reflexaoDiaRepository).save(captor.capture());
        assertThat(captor.getValue().getMes()).isEqualTo(7);
        assertThat(captor.getValue().getDia()).isEqualTo(12);
        assertThat(captor.getValue().getTexto()).isEqualTo("texto novo");
        assertThat(resultado.texto()).isEqualTo("texto novo");
    }

    @Test
    void salvar_deveEditarReflexaoExistente() {
        ReflexaoDia existente = new ReflexaoDia(7, 12, "texto antigo");
        when(reflexaoDiaRepository.findByMesAndDia(7, 12)).thenReturn(Optional.of(existente));
        when(reflexaoDiaRepository.save(any(ReflexaoDia.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SalvarReflexaoDiaRequest request = new SalvarReflexaoDiaRequest("texto editado");
        ReflexaoDiaResponse resultado = reflexaoDiaService.salvar(7, 12, request);
        assertThat(resultado.texto()).isEqualTo("texto editado");
        verify(reflexaoDiaRepository).save(existente);
    }

    @Test
    void salvar_deveLancarValidationExceptionParaDataInvalida() {
        SalvarReflexaoDiaRequest request = new SalvarReflexaoDiaRequest("texto");
        assertThatThrownBy(() -> reflexaoDiaService.salvar(4, 31, request))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(reflexaoDiaRepository);
    }
}

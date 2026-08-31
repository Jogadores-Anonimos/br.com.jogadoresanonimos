package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineAgendaItemRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineResponse;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniaoOnline;
import br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.ReuniaoOnline;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoOnlineRepository;
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
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoOnlineServiceTest {

    @Mock
    ReuniaoOnlineRepository reuniaoOnlineRepository;

    ReuniaoOnlineService reuniaoOnlineService;

    @BeforeEach
    void setUp() {
        reuniaoOnlineService = new ReuniaoOnlineService(reuniaoOnlineRepository);
    }

    private ReuniaoOnline reuniaoOnline() {
        return new ReuniaoOnline("Grupo Online Quinta", "5511994721933",
                List.of(new AgendaItemReuniaoOnline(DiaSemanaReuniao.QUINTA, "20h00")));
    }

    private CriarReuniaoOnlineRequest criarRequest() {
        return new CriarReuniaoOnlineRequest("Grupo Online Quinta", "5511994721933",
                List.of(new ReuniaoOnlineAgendaItemRequest("QUINTA", "20h00")));
    }

    private EditarReuniaoOnlineRequest editarRequest() {
        return new EditarReuniaoOnlineRequest("Grupo Online Quinta editado", "  ",
                List.of(new ReuniaoOnlineAgendaItemRequest("SABADO", "10h00")));
    }

    @Test
    void listarTodas_deveDelegarParaRepositoryEMapear() {
        ReuniaoOnline entidade = reuniaoOnline();
        when(reuniaoOnlineRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(entidade));
        List<ReuniaoOnlineResponse> resultado = reuniaoOnlineService.listarTodas();
        assertThat(resultado).extracting(ReuniaoOnlineResponse::nome).containsExactly("Grupo Online Quinta");
    }

    @Test
    void criar_devePersistirComNomeTrimEAgendaOrdenada() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("  Grupo Online  ", "5511994721933",
                List.of(new ReuniaoOnlineAgendaItemRequest("SABADO", "10h00"),
                        new ReuniaoOnlineAgendaItemRequest("QUINTA", "20h00")));
        ArgumentCaptor<ReuniaoOnline> captor = ArgumentCaptor.forClass(ReuniaoOnline.class);
        when(reuniaoOnlineRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        ReuniaoOnlineResponse resultado = reuniaoOnlineService.criar(request);

        ReuniaoOnline persistido = captor.getValue();
        assertThat(persistido.getNome()).isEqualTo("Grupo Online");
        assertThat(persistido.getTelefone()).isEqualTo("5511994721933");
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("QUINTA", "20h00"), tuple("SABADO", "10h00"));
    }

    @Test
    void criar_deveNormalizarTelefoneEmBrancoParaNull() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online", "   ",
                List.of(new ReuniaoOnlineAgendaItemRequest("QUINTA", "20h00")));
        ArgumentCaptor<ReuniaoOnline> captor = ArgumentCaptor.forClass(ReuniaoOnline.class);
        when(reuniaoOnlineRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        reuniaoOnlineService.criar(request);

        assertThat(captor.getValue().getTelefone()).isNull();
    }

    @Test
    void criar_deveFalharQuandoAgendaTemDiaInvalido() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online", null,
                List.of(new ReuniaoOnlineAgendaItemRequest("FERIADO", "20h00")));

        assertThatThrownBy(() -> reuniaoOnlineService.criar(request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void criar_deveFalharQuandoAgendaRepeteCombinacao() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online", null,
                List.of(new ReuniaoOnlineAgendaItemRequest("QUINTA", "20h00"),
                        new ReuniaoOnlineAgendaItemRequest("QUINTA", "20h00")));

        assertThatThrownBy(() -> reuniaoOnlineService.criar(request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void editar_deveAtualizarEntidadeExistente() {
        UUID id = UUID.randomUUID();
        ReuniaoOnline existente = reuniaoOnline();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.of(existente));

        ReuniaoOnlineResponse resultado = reuniaoOnlineService.editar(id, editarRequest());

        assertThat(resultado.nome()).isEqualTo("Grupo Online Quinta editado");
        assertThat(resultado.telefone()).isNull();
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("SABADO", "10h00"));
    }

    @Test
    void editar_deveFalharQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reuniaoOnlineService.editar(id, editarRequest()))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void remover_deveDeletarQuandoExiste() {
        UUID id = UUID.randomUUID();
        ReuniaoOnline existente = reuniaoOnline();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.of(existente));

        reuniaoOnlineService.remover(id);

        verify(reuniaoOnlineRepository).delete(existente);
    }

    @Test
    void remover_deveFalharQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reuniaoOnlineService.remover(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}

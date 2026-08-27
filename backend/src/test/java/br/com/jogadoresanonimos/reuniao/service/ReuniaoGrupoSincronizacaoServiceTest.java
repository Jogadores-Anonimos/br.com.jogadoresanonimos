package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.escritorio.entity.AgendaItemGrupo;
import br.com.jogadoresanonimos.escritorio.entity.DiaSemana;
import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoCodigo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.escritorio.repository.GrupoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniao;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReuniaoGrupoSincronizacaoServiceTest {

    @Mock ReuniaoRepository reuniaoRepository;
    @Mock GrupoIntergrupoRepository grupoRepository;
    @Mock EstadoIntergrupoRepository estadoRepository;
    ReuniaoGrupoSincronizacaoService service;

    @BeforeEach
    void setUp() {
        service = new ReuniaoGrupoSincronizacaoService(reuniaoRepository, grupoRepository, estadoRepository);
    }

    @Test
    void criarReuniaoCanonica_deveVincularEConverterAgenda() {
        GrupoIntergrupo grupo = novoGrupo();
        when(reuniaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reuniao reuniao = service.criarReuniaoCanonica(grupo, endereco("Rua antiga", -23.5, -46.6, true));

        assertThat(reuniao.getGrupoIntergrupo()).isSameAs(grupo);
        assertThat(reuniao.getCategoria()).isEqualTo(CategoriaReuniao.presencial);
        assertThat(reuniao.getAgenda()).extracting("diaSemana", "horario")
                .containsExactly(org.assertj.core.groups.Tuple.tuple(DiaSemanaReuniao.SEGUNDA, "18h"));
        assertThat(reuniao.getPeriodo()).isEqualTo("Noite");
    }

    @Test
    void criarGrupoParaReuniaoPresencial_deveIgnorarReuniaoVirtual() {
        Reuniao virtual = novaReuniao(CategoriaReuniao.virtual);

        service.criarGrupoParaReuniaoPresencial(virtual);

        assertThat(virtual.getGrupoIntergrupo()).isNull();
        verifyNoInteractions(grupoRepository);
    }

    @Test
    void criarGrupoParaReuniaoPresencial_deveCriarGrupoEVincularReuniaoPresencial() {
        RegiaoIntergrupo regiao = new RegiaoIntergrupo("Sudeste", RegiaoCodigo.SUDESTE, 0);
        EstadoIntergrupo estado = new EstadoIntergrupo(regiao, UnidadeFederativa.SP, 0);
        setId(estado, UUID.randomUUID());
        Reuniao presencial = novaReuniao(CategoriaReuniao.presencial);
        when(estadoRepository.findByUf(UnidadeFederativa.SP)).thenReturn(Optional.of(estado));
        when(estadoRepository.buscarPorIdComLock(estado.getId())).thenReturn(Optional.of(estado));
        when(grupoRepository.buscarMaiorOrdemPorEstadoId(estado.getId())).thenReturn(2);
        when(grupoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.criarGrupoParaReuniaoPresencial(presencial);

        ArgumentCaptor<GrupoIntergrupo> captor = ArgumentCaptor.forClass(GrupoIntergrupo.class);
        verify(grupoRepository).save(captor.capture());
        assertThat(captor.getValue().getOrdem()).isEqualTo(3);
        assertThat(presencial.getGrupoIntergrupo()).isSameAs(captor.getValue());
    }

    @Test
    void sincronizarEdicaoPorGrupo_deveAtualizarCanonicaELimparCoordenadas() {
        GrupoIntergrupo grupo = novoGrupo();
        Reuniao reuniao = novaReuniao(CategoriaReuniao.presencial);
        reuniao.vincularGrupoIntergrupo(grupo);
        when(reuniaoRepository.buscarPorGrupoIntergrupoIdComLock(grupo.getId()))
                .thenReturn(Optional.of(reuniao));
        when(grupoRepository.buscarPorIdComLock(grupo.getId())).thenReturn(Optional.of(grupo));

        Optional<GrupoIntergrupo> sincronizado = service.sincronizarEdicaoPorGrupo(grupo.getId(), "Nome novo", "Rua nova",
                new DadosEnderecoReuniao(null, null, null, "Bairro novo", "Cidade nova", "SP",
                        null, null, false),
                List.of(new AgendaItemGrupo(DiaSemana.SEGUNDA, "20h")), null);

        assertThat(sincronizado).contains(grupo);
        assertThat(reuniao.getNome()).isEqualTo("Nome novo");
        assertThat(reuniao.getLatitude()).isNull();
        assertThat(reuniao.getLongitude()).isNull();
        assertThat(reuniao.getCep()).isNull();
        assertThat(reuniao.getNumero()).isNull();
        assertThat(reuniao.getLogradouro()).isNull();
        assertThat(reuniao.getPeriodo()).isEqualTo("Noite");
        assertThat(grupo.getNome()).isEqualTo("Nome novo");
        assertThat(grupo.getAgenda()).extracting("horario").containsExactly("20h");
    }

    @Test
    void sincronizarEdicaoPorGrupo_deveRejeitarVersaoDesatualizada() {
        GrupoIntergrupo grupo = novoGrupo();
        Reuniao reuniao = novaReuniao(CategoriaReuniao.presencial);
        reuniao.vincularGrupoIntergrupo(grupo);
        when(reuniaoRepository.buscarPorGrupoIntergrupoIdComLock(grupo.getId()))
                .thenReturn(Optional.of(reuniao));
        when(grupoRepository.buscarPorIdComLock(grupo.getId())).thenReturn(Optional.of(grupo));

        assertThatThrownBy(() -> service.sincronizarEdicaoPorGrupo(grupo.getId(), "Nome", "Rua",
                endereco("Rua", null, null, false), grupo.getAgenda(), Instant.EPOCH))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void criarReuniaoCanonica_deveOrdenarDomingoAntesDeSegundaECalcularManha() {
        GrupoIntergrupo grupo = novoGrupo(List.of(
                new AgendaItemGrupo(DiaSemana.SEGUNDA, "20h"),
                new AgendaItemGrupo(DiaSemana.DOMINGO, "08h")));
        when(reuniaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reuniao reuniao = service.criarReuniaoCanonica(grupo, endereco("Rua antiga", null, null, false));

        assertThat(reuniao.getAgenda()).extracting("diaSemana", "horario")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(DiaSemanaReuniao.DOMINGO, "08h"),
                        org.assertj.core.groups.Tuple.tuple(DiaSemanaReuniao.SEGUNDA, "20h"));
        assertThat(reuniao.getDiaSemana()).isEqualTo("Domingo");
        assertThat(reuniao.getPeriodo()).isEqualTo("Manhã");
    }

    @Test
    void sincronizarAposEdicaoReuniao_deveBloquearUfDivergente() {
        GrupoIntergrupo grupo = novoGrupo();
        Reuniao reuniao = new Reuniao("Grupo", CategoriaReuniao.presencial, "Descrição", "Segunda", "18h",
                "Rua", null, "Segunda", null, null, null, null, null, null,
                "Bairro", "Cidade", "RJ", null);
        reuniao.substituirAgenda(List.of(new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "18h")));
        reuniao.vincularGrupoIntergrupo(grupo);
        when(grupoRepository.buscarPorIdComLock(grupo.getId())).thenReturn(Optional.of(grupo));

        assertThatThrownBy(() -> service.sincronizarAposEdicaoReuniao(reuniao))
                .isInstanceOf(ConflitoDeEstadoException.class)
                .hasMessageContaining("UF");
    }

    @Test
    void removerPorReuniaoNaoVinculada_naoDeveApagarGrupoAlgum() {
        Reuniao reuniao = novaReuniao(CategoriaReuniao.presencial);

        service.removerPorReuniao(reuniao);

        verify(reuniaoRepository).delete(reuniao);
        verify(reuniaoRepository).flush();
        verifyNoInteractions(grupoRepository);
    }

    @Test
    void bloquearReuniaoParaEscrita_deveRevalidarVinculoAposEsperarLock() {
        GrupoIntergrupo grupo = novoGrupo();
        Reuniao reuniao = novaReuniao(CategoriaReuniao.presencial);
        reuniao.vincularGrupoIntergrupo(grupo);
        when(reuniaoRepository.buscarGrupoIntergrupoIdPorReuniaoId(reuniao.getId()))
                .thenReturn(Optional.empty());
        when(reuniaoRepository.buscarPorIdComLock(reuniao.getId())).thenReturn(Optional.of(reuniao));

        assertThatThrownBy(() -> service.bloquearReuniaoParaEscrita(reuniao.getId()))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void bloquearReuniaoParaEscrita_deveBloquearEstadoGrupoEReuniaoNestaOrdem() {
        GrupoIntergrupo grupo = novoGrupo();
        Reuniao reuniao = novaReuniao(CategoriaReuniao.presencial);
        reuniao.vincularGrupoIntergrupo(grupo);
        when(reuniaoRepository.buscarGrupoIntergrupoIdPorReuniaoId(reuniao.getId()))
                .thenReturn(Optional.of(grupo.getId()));
        when(reuniaoRepository.buscarPorIdComLock(reuniao.getId())).thenReturn(Optional.of(reuniao));

        assertThat(service.bloquearReuniaoParaEscrita(reuniao.getId())).isSameAs(reuniao);

        var ordem = inOrder(estadoRepository, grupoRepository, reuniaoRepository);
        ordem.verify(estadoRepository).buscarPorIdComLock(grupo.getEstado().getId());
        ordem.verify(grupoRepository).buscarPorIdComLock(grupo.getId());
        ordem.verify(reuniaoRepository).buscarPorIdComLock(reuniao.getId());
    }

    private GrupoIntergrupo novoGrupo() {
        return novoGrupo(List.of(new AgendaItemGrupo(DiaSemana.SEGUNDA, "18h")));
    }

    private GrupoIntergrupo novoGrupo(List<AgendaItemGrupo> agenda) {
        RegiaoIntergrupo regiao = new RegiaoIntergrupo("Sudeste", RegiaoCodigo.SUDESTE, 0);
        EstadoIntergrupo estado = new EstadoIntergrupo(regiao, UnidadeFederativa.SP, 0);
        setId(estado, UUID.randomUUID());
        GrupoIntergrupo grupo = new GrupoIntergrupo(estado, "Grupo", "Rua antiga", "Bairro", "Cidade",
                agenda, 0);
        setId(grupo, UUID.randomUUID());
        lenient().when(grupoRepository.buscarEstadoIdPorGrupoId(grupo.getId()))
                .thenReturn(Optional.of(estado.getId()));
        lenient().when(estadoRepository.buscarPorIdComLock(estado.getId())).thenReturn(Optional.of(estado));
        lenient().when(grupoRepository.buscarPorIdComLock(grupo.getId())).thenReturn(Optional.of(grupo));
        return grupo;
    }

    private Reuniao novaReuniao(CategoriaReuniao categoria) {
        Reuniao reuniao = new Reuniao("Grupo", categoria, "Descrição", "Segunda", "18h",
                "Rua antiga", null, "Segunda", null, -23.5, -46.6, "01310-100", "10", "Rua antiga",
                "Bairro", "Cidade", "SP", null);
        reuniao.substituirAgenda(List.of(new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "18h")));
        setId(reuniao, UUID.randomUUID());
        return reuniao;
    }

    private DadosEnderecoReuniao endereco(String logradouro, Double latitude, Double longitude,
                                            boolean detalhada) {
        return new DadosEnderecoReuniao("01310-100", "10", logradouro, "Bairro", "Cidade", "SP",
                latitude, longitude, detalhada);
    }

    private void setId(Object alvo, UUID id) {
        try {
            Field campo = alvo.getClass().getDeclaredField("id");
            campo.setAccessible(true);
            campo.set(alvo, id);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}

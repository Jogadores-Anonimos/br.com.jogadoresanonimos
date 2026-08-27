package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoCodigo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.GrupoIntergrupoRepository;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoGrupoBackfillParItemServiceTest {

    @Mock
    ReuniaoRepository reuniaoRepository;

    @Mock
    GrupoIntergrupoRepository grupoRepository;

    @Mock
    EstadoIntergrupoRepository estadoRepository;

    @Mock
    ReuniaoGrupoSincronizacaoService sincronizacaoService;

    ReuniaoGrupoBackfillParItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ReuniaoGrupoBackfillParItemService(reuniaoRepository, grupoRepository, estadoRepository,
                sincronizacaoService);
    }

    private Reuniao reuniaoOrfaComEstado(String estado) {
        Reuniao reuniao = new Reuniao("Grupo Centro", CategoriaReuniao.presencial, "descricao", "Segunda-feira",
                "19h30", "Rua das Flores, 123", null, null, null,
                -23.5505, -46.6333, null, null, null, "Centro", "São Paulo", estado, null);
        ReflectionTestUtils.setField(reuniao, "id", UUID.randomUUID());
        return reuniao;
    }

    private EstadoIntergrupo estadoIntergrupo(UnidadeFederativa uf) {
        RegiaoIntergrupo regiao = new RegiaoIntergrupo("Sudeste", RegiaoCodigo.SUDESTE, 1);
        EstadoIntergrupo estado = new EstadoIntergrupo(regiao, uf, 1);
        ReflectionTestUtils.setField(estado, "id", UUID.randomUUID());
        return estado;
    }

    private GrupoIntergrupo grupoOrfao() {
        GrupoIntergrupo grupo = new GrupoIntergrupo(estadoIntergrupo(UnidadeFederativa.SP), "Grupo Espelho",
                "Rua Nova, 45", "Centro", "São Paulo", List.of(), 1);
        ReflectionTestUtils.setField(grupo, "id", UUID.randomUUID());
        return grupo;
    }

    @Test
    void criarGrupoParaReuniaoOrfaSeAindaOrfa_criaGrupoQuandoReuniaoAindaSemVinculo() {
        Reuniao reuniao = reuniaoOrfaComEstado("SP");
        EstadoIntergrupo estado = estadoIntergrupo(UnidadeFederativa.SP);
        GrupoIntergrupo grupoCriado = grupoOrfao();
        when(reuniaoRepository.buscarPorIdComLock(reuniao.getId())).thenReturn(Optional.of(reuniao));
        when(estadoRepository.findByUf(UnidadeFederativa.SP)).thenReturn(Optional.of(estado));
        when(grupoRepository.buscarMaiorOrdemPorEstadoId(estado.getId())).thenReturn(-1);
        when(sincronizacaoService.criarGrupoParaReuniaoOrfa(reuniao, estado, 0)).thenReturn(grupoCriado);

        Optional<UUID> resultado = itemService.criarGrupoParaReuniaoOrfaSeAindaOrfa(reuniao.getId());

        assertThat(resultado).contains(grupoCriado.getId());
        verify(sincronizacaoService).criarGrupoParaReuniaoOrfa(reuniao, estado, 0);
    }

    @Test
    void criarGrupoParaReuniaoOrfaSeAindaOrfa_ordemConsideraGapsExistentesUsandoMaiorOrdemMaisUm() {
        Reuniao reuniao = reuniaoOrfaComEstado("SP");
        EstadoIntergrupo estado = estadoIntergrupo(UnidadeFederativa.SP);
        GrupoIntergrupo grupoCriado = grupoOrfao();
        when(reuniaoRepository.buscarPorIdComLock(reuniao.getId())).thenReturn(Optional.of(reuniao));
        when(estadoRepository.findByUf(UnidadeFederativa.SP)).thenReturn(Optional.of(estado));
        when(grupoRepository.buscarMaiorOrdemPorEstadoId(estado.getId())).thenReturn(3);
        when(sincronizacaoService.criarGrupoParaReuniaoOrfa(reuniao, estado, 4)).thenReturn(grupoCriado);

        Optional<UUID> resultado = itemService.criarGrupoParaReuniaoOrfaSeAindaOrfa(reuniao.getId());

        assertThat(resultado).contains(grupoCriado.getId());
        verify(sincronizacaoService).criarGrupoParaReuniaoOrfa(reuniao, estado, 4);
    }

    @Test
    void criarGrupoParaReuniaoOrfaSeAindaOrfa_reuniaoInexistente_naoCriaNada() {
        UUID reuniaoId = UUID.randomUUID();
        when(reuniaoRepository.buscarPorIdComLock(reuniaoId)).thenReturn(Optional.empty());

        Optional<UUID> resultado = itemService.criarGrupoParaReuniaoOrfaSeAindaOrfa(reuniaoId);

        assertThat(resultado).isEmpty();
        verifyNoInteractions(sincronizacaoService);
        verify(estadoRepository, never()).findByUf(any());
    }

    @Test
    void criarGrupoParaReuniaoOrfaSeAindaOrfa_reuniaoJaVinculada_ehIdempotenteNaoDuplicaPar() {
        Reuniao reuniao = reuniaoOrfaComEstado("SP");
        GrupoIntergrupo grupoJaVinculado = grupoOrfao();
        reuniao.vincularGrupoIntergrupo(grupoJaVinculado);
        when(reuniaoRepository.buscarPorIdComLock(reuniao.getId())).thenReturn(Optional.of(reuniao));

        Optional<UUID> resultado = itemService.criarGrupoParaReuniaoOrfaSeAindaOrfa(reuniao.getId());

        assertThat(resultado).isEmpty();
        verifyNoInteractions(sincronizacaoService);
    }

    @Test
    void criarGrupoParaReuniaoOrfaSeAindaOrfa_estadoOrganizacionalNaoEncontrado_lancaExcecaoIsolada() {
        Reuniao reuniao = reuniaoOrfaComEstado("SP");
        when(reuniaoRepository.buscarPorIdComLock(reuniao.getId())).thenReturn(Optional.of(reuniao));
        when(estadoRepository.findByUf(UnidadeFederativa.SP)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.criarGrupoParaReuniaoOrfaSeAindaOrfa(reuniao.getId()))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verifyNoInteractions(sincronizacaoService);
    }

    @Test
    void criarReuniaoParaGrupoOrfaoSeAindaOrfao_criaReuniaoQuandoGrupoAindaSemVinculo() {
        GrupoIntergrupo grupo = grupoOrfao();
        Reuniao reuniaoCriada = reuniaoOrfaComEstado("SP");
        when(grupoRepository.buscarPorIdComLock(grupo.getId())).thenReturn(Optional.of(grupo));
        when(reuniaoRepository.findByGrupoIntergrupoId(grupo.getId())).thenReturn(Optional.empty());
        when(sincronizacaoService.criarReuniaoCanonica(eq(grupo), any(DadosEnderecoReuniao.class)))
                .thenReturn(reuniaoCriada);

        Optional<UUID> resultado = itemService.criarReuniaoParaGrupoOrfaoSeAindaOrfao(grupo.getId());

        assertThat(resultado).contains(reuniaoCriada.getId());
        verify(sincronizacaoService).criarReuniaoCanonica(eq(grupo), any(DadosEnderecoReuniao.class));
    }

    @Test
    void criarReuniaoParaGrupoOrfaoSeAindaOrfao_grupoInexistente_naoCriaNada() {
        UUID grupoId = UUID.randomUUID();
        when(grupoRepository.buscarPorIdComLock(grupoId)).thenReturn(Optional.empty());

        Optional<UUID> resultado = itemService.criarReuniaoParaGrupoOrfaoSeAindaOrfao(grupoId);

        assertThat(resultado).isEmpty();
        verifyNoInteractions(sincronizacaoService);
    }

    @Test
    void criarReuniaoParaGrupoOrfaoSeAindaOrfao_grupoJaVinculado_ehIdempotenteNaoDuplicaPar() {
        GrupoIntergrupo grupo = grupoOrfao();
        Reuniao reuniaoVinculada = reuniaoOrfaComEstado("SP");
        when(grupoRepository.buscarPorIdComLock(grupo.getId())).thenReturn(Optional.of(grupo));
        when(reuniaoRepository.findByGrupoIntergrupoId(grupo.getId())).thenReturn(Optional.of(reuniaoVinculada));

        Optional<UUID> resultado = itemService.criarReuniaoParaGrupoOrfaoSeAindaOrfao(grupo.getId());

        assertThat(resultado).isEmpty();
        verifyNoInteractions(sincronizacaoService);
    }
}

package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoCodigo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoGrupoBackfillParServiceTest {

    @Mock
    ReuniaoRepository reuniaoRepository;

    @Mock
    GrupoIntergrupoRepository grupoRepository;

    @Mock
    ReuniaoGrupoBackfillParItemService itemService;

    ReuniaoGrupoBackfillParService service;

    @BeforeEach
    void setUp() {
        service = new ReuniaoGrupoBackfillParService(reuniaoRepository, grupoRepository, itemService);
    }

    private Reuniao reuniaoOrfa() {
        Reuniao reuniao = new Reuniao("Grupo Centro", CategoriaReuniao.presencial, "descricao", "Segunda-feira",
                "19h30", "Rua das Flores, 123", null, null, null,
                -23.5505, -46.6333, null, null, null, null, "São Paulo", "SP", null);
        ReflectionTestUtils.setField(reuniao, "id", UUID.randomUUID());
        return reuniao;
    }

    private GrupoIntergrupo grupoOrfao() {
        RegiaoIntergrupo regiao = new RegiaoIntergrupo("Sudeste", RegiaoCodigo.SUDESTE, 1);
        EstadoIntergrupo estado = new EstadoIntergrupo(regiao, UnidadeFederativa.SP, 1);
        GrupoIntergrupo grupo = new GrupoIntergrupo(estado, "Grupo Espelho", "Rua Nova, 45", "Centro",
                "São Paulo", List.of(), 1);
        ReflectionTestUtils.setField(grupo, "id", UUID.randomUUID());
        return grupo;
    }

    @Test
    void dryRun_naoPersisteNadaMesmoComOrfaosDetectados() {
        Reuniao reuniaoOrfa = reuniaoOrfa();
        GrupoIntergrupo grupoOrfao = grupoOrfao();
        when(reuniaoRepository.findAllByCategoriaAndGrupoIntergrupoIsNull(CategoriaReuniao.presencial))
                .thenReturn(List.of(reuniaoOrfa));
        when(grupoRepository.buscarOrfaosSemReuniao()).thenReturn(List.of(grupoOrfao));

        ResultadoBackfillParReuniaoGrupo resultado = service.executar(true);

        assertThat(resultado.dryRun()).isTrue();
        assertThat(resultado.reuniaoIdsOrfasDetectadas()).containsExactly(reuniaoOrfa.getId());
        assertThat(resultado.grupoIdsOrfaosDetectados()).containsExactly(grupoOrfao.getId());
        assertThat(resultado.grupoIdsCriados()).isEmpty();
        assertThat(resultado.reuniaoIdsCriadas()).isEmpty();
        verifyNoInteractions(itemService);
    }

    @Test
    void apply_criaParesFaltantesParaReuniaoEGrupoOrfaos() {
        Reuniao reuniaoOrfa = reuniaoOrfa();
        GrupoIntergrupo grupoOrfao = grupoOrfao();
        UUID grupoCriadoId = UUID.randomUUID();
        UUID reuniaoCriadaId = UUID.randomUUID();
        when(reuniaoRepository.findAllByCategoriaAndGrupoIntergrupoIsNull(CategoriaReuniao.presencial))
                .thenReturn(List.of(reuniaoOrfa));
        when(grupoRepository.buscarOrfaosSemReuniao()).thenReturn(List.of(grupoOrfao));
        when(itemService.criarGrupoParaReuniaoOrfaSeAindaOrfa(reuniaoOrfa.getId()))
                .thenReturn(Optional.of(grupoCriadoId));
        when(itemService.criarReuniaoParaGrupoOrfaoSeAindaOrfao(grupoOrfao.getId()))
                .thenReturn(Optional.of(reuniaoCriadaId));

        ResultadoBackfillParReuniaoGrupo resultado = service.executar(false);

        assertThat(resultado.dryRun()).isFalse();
        assertThat(resultado.grupoIdsCriados()).containsExactly(grupoCriadoId);
        assertThat(resultado.reuniaoIdsCriadas()).containsExactly(reuniaoCriadaId);
        verify(itemService).criarGrupoParaReuniaoOrfaSeAindaOrfa(reuniaoOrfa.getId());
        verify(itemService).criarReuniaoParaGrupoOrfaoSeAindaOrfao(grupoOrfao.getId());
    }

    @Test
    void apply_executadoDuasVezes_naoDuplicaParesJaResolvidosNaSegundaRodada() {
        Reuniao reuniaoOrfa = reuniaoOrfa();
        UUID grupoCriadoId = UUID.randomUUID();
        when(reuniaoRepository.findAllByCategoriaAndGrupoIntergrupoIsNull(CategoriaReuniao.presencial))
                .thenReturn(List.of(reuniaoOrfa), List.of());
        when(grupoRepository.buscarOrfaosSemReuniao()).thenReturn(List.of(), List.of());
        when(itemService.criarGrupoParaReuniaoOrfaSeAindaOrfa(reuniaoOrfa.getId()))
                .thenReturn(Optional.of(grupoCriadoId));

        ResultadoBackfillParReuniaoGrupo primeiraExecucao = service.executar(false);
        ResultadoBackfillParReuniaoGrupo segundaExecucao = service.executar(false);

        assertThat(primeiraExecucao.grupoIdsCriados()).containsExactly(grupoCriadoId);
        assertThat(segundaExecucao.reuniaoIdsOrfasDetectadas()).isEmpty();
        assertThat(segundaExecucao.grupoIdsCriados()).isEmpty();
        assertThat(segundaExecucao.reuniaoIdsCriadas()).isEmpty();
    }

    @Test
    void semOrfaos_naoChamaItemServiceEmNenhumModo() {
        when(reuniaoRepository.findAllByCategoriaAndGrupoIntergrupoIsNull(CategoriaReuniao.presencial))
                .thenReturn(List.of());
        when(grupoRepository.buscarOrfaosSemReuniao()).thenReturn(List.of());

        ResultadoBackfillParReuniaoGrupo resultado = service.executar(false);

        assertThat(resultado.grupoIdsCriados()).isEmpty();
        assertThat(resultado.reuniaoIdsCriadas()).isEmpty();
        verifyNoInteractions(itemService);
    }
}

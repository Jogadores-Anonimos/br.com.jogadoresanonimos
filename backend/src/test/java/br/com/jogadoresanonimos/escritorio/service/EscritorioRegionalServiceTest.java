package br.com.jogadoresanonimos.escritorio.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.escritorio.dto.*;
import br.com.jogadoresanonimos.escritorio.entity.AgendaItemGrupo;
import br.com.jogadoresanonimos.escritorio.entity.DiaSemana;
import br.com.jogadoresanonimos.escritorio.entity.EscritorioServico;
import br.com.jogadoresanonimos.escritorio.entity.EscritorioServicoTelefone;
import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.escritorio.repository.EscritorioServicoRepository;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.GrupoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.RegiaoIntergrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscritorioRegionalServiceTest {

    @Mock
    RegiaoIntergrupoRepository regiaoRepository;

    @Mock
    EstadoIntergrupoRepository estadoRepository;

    @Mock
    GrupoIntergrupoRepository grupoRepository;

    @Mock
    EscritorioServicoRepository escritorioServicoRepository;

    EscritorioRegionalService service;

    @BeforeEach
    void setUp() {
        service = new EscritorioRegionalService(regiaoRepository, estadoRepository, grupoRepository,
                escritorioServicoRepository);
    }

    private RegiaoIntergrupo novaRegiao() {
        return new RegiaoIntergrupo("Região Sudeste", 0);
    }

    private EstadoIntergrupo novoEstado(RegiaoIntergrupo regiao) {
        return new EstadoIntergrupo(regiao, UnidadeFederativa.SP, 0);
    }

    private GrupoIntergrupo novoGrupo(EstadoIntergrupo estado) {
        return new GrupoIntergrupo(estado, "Grupo", "Rua", null, "Cidade",
                List.of(new AgendaItemGrupo(DiaSemana.SEGUNDA, "Seg 20h")), 0);
    }

    @Test
    void listarEstruturaCompleta_deveCombinarEscritoriosERegioes() {
        when(escritorioServicoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of());
        when(regiaoRepository.findAllComHierarquiaCompleta()).thenReturn(List.of(novaRegiao()));
        EscritoriosRegionaisResponse resultado = service.listarEstruturaCompleta();
        assertThat(resultado.regioes()).hasSize(1);
        assertThat(resultado.escritorios()).isEmpty();
    }

    @Test
    void editarRegiao_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(regiaoRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.editarRegiao(id, new EditarRegiaoRequest("nova")))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void moverRegiaoParaCima_deveTrocarOrdemComVizinhoAnterior() {
        UUID id = UUID.randomUUID();
        RegiaoIntergrupo primeira = novaRegiao();
        RegiaoIntergrupo segunda = new RegiaoIntergrupo("Região Sul", 1);
        setId(segunda, id);
        when(regiaoRepository.findById(id)).thenReturn(Optional.of(segunda));
        when(regiaoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeira, segunda));
        RegiaoIntergrupoResponse resultado = service.moverRegiaoParaCima(id);
        assertThat(resultado.ordem()).isEqualTo(0);
        assertThat(primeira.getOrdem()).isEqualTo(1);
    }

    @Test
    void moverRegiaoParaCima_deveSerNoOpQuandoJaEhPrimeira() {
        UUID id = UUID.randomUUID();
        RegiaoIntergrupo primeira = novaRegiao();
        setId(primeira, id);
        RegiaoIntergrupo segunda = new RegiaoIntergrupo("Região Sul", 1);
        when(regiaoRepository.findById(id)).thenReturn(Optional.of(primeira));
        when(regiaoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeira, segunda));
        RegiaoIntergrupoResponse resultado = service.moverRegiaoParaCima(id);
        assertThat(resultado.ordem()).isEqualTo(0);
        assertThat(segunda.getOrdem()).isEqualTo(1);
    }

    @Test
    void moverRegiaoParaBaixo_deveSerNoOpQuandoJaEhUltima() {
        UUID id = UUID.randomUUID();
        RegiaoIntergrupo primeira = novaRegiao();
        RegiaoIntergrupo segunda = new RegiaoIntergrupo("Região Sul", 1);
        setId(segunda, id);
        when(regiaoRepository.findById(id)).thenReturn(Optional.of(segunda));
        when(regiaoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeira, segunda));
        RegiaoIntergrupoResponse resultado = service.moverRegiaoParaBaixo(id);
        assertThat(resultado.ordem()).isEqualTo(1);
        assertThat(primeira.getOrdem()).isEqualTo(0);
    }

    @Test
    void criarEstado_deveLancarExcecaoQuandoRegiaoNaoExiste() {
        UUID regiaoId = UUID.randomUUID();
        when(regiaoRepository.findById(regiaoId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.criarEstado(regiaoId, new CriarEstadoRequest("SP")))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void criarEstado_deveLancarExcecaoQuandoUfInvalida() {
        UUID regiaoId = UUID.randomUUID();
        when(regiaoRepository.findById(regiaoId)).thenReturn(Optional.of(novaRegiao()));
        assertThatThrownBy(() -> service.criarEstado(regiaoId, new CriarEstadoRequest("XX")))
                .isInstanceOf(ValidationException.class);
        verify(estadoRepository, never()).save(any());
    }

    @Test
    void criarEstado_deveLancarConflitoQuandoUfDuplicadaNaMesmaRegiao() {
        UUID regiaoId = UUID.randomUUID();
        when(regiaoRepository.findById(regiaoId)).thenReturn(Optional.of(novaRegiao()));
        when(estadoRepository.existsByRegiaoIdAndUf(regiaoId, UnidadeFederativa.SP)).thenReturn(true);
        assertThatThrownBy(() -> service.criarEstado(regiaoId, new CriarEstadoRequest("SP")))
                .isInstanceOf(ConflitoDeEstadoException.class);
        verify(estadoRepository, never()).save(any());
    }

    @Test
    void criarEstado_deveSalvarQuandoUfValidaENaoDuplicada() {
        UUID regiaoId = UUID.randomUUID();
        RegiaoIntergrupo regiao = novaRegiao();
        when(regiaoRepository.findById(regiaoId)).thenReturn(Optional.of(regiao));
        when(estadoRepository.existsByRegiaoIdAndUf(regiaoId, UnidadeFederativa.SP)).thenReturn(false);
        when(estadoRepository.findAllByRegiaoIdOrderByOrdemAsc(regiaoId)).thenReturn(List.of());
        when(estadoRepository.save(any(EstadoIntergrupo.class))).thenAnswer(inv -> inv.getArgument(0));
        EstadoIntergrupoResponse resultado = service.criarEstado(regiaoId, new CriarEstadoRequest("sp"));
        assertThat(resultado.uf()).isEqualTo("SP");
        assertThat(resultado.nome()).isEqualTo("São Paulo");
    }

    @Test
    void editarEstado_naoDeveValidarConflitoQuandoUfNaoMudou() {
        UUID regiaoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        RegiaoIntergrupo regiao = novaRegiao();
        setId(regiao, regiaoId);
        EstadoIntergrupo estado = novoEstado(regiao);
        setId(estado, id);
        when(estadoRepository.findById(id)).thenReturn(Optional.of(estado));
        EstadoIntergrupoResponse resultado = service.editarEstado(regiaoId, id, new EditarEstadoRequest("SP"));
        assertThat(resultado.uf()).isEqualTo("SP");
        verify(estadoRepository, never()).existsByRegiaoIdAndUf(any(), any());
    }

    @Test
    void editarEstado_deveLancarConflitoQuandoTrocaParaUfJaUsadaNaRegiao() {
        UUID regiaoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        RegiaoIntergrupo regiao = novaRegiao();
        setId(regiao, regiaoId);
        EstadoIntergrupo estado = novoEstado(regiao);
        setId(estado, id);
        when(estadoRepository.findById(id)).thenReturn(Optional.of(estado));
        when(estadoRepository.existsByRegiaoIdAndUf(regiaoId, UnidadeFederativa.RJ)).thenReturn(true);
        assertThatThrownBy(() -> service.editarEstado(regiaoId, id, new EditarEstadoRequest("RJ")))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void removerEstado_deveLancarExcecaoQuandoEstadoNaoPertenceARegiao() {
        UUID regiaoId = UUID.randomUUID();
        UUID outraRegiaoId = UUID.randomUUID();
        UUID estadoId = UUID.randomUUID();
        RegiaoIntergrupo outraRegiao = novaRegiao();
        setId(outraRegiao, outraRegiaoId);
        EstadoIntergrupo estado = novoEstado(outraRegiao);
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estado));
        assertThatThrownBy(() -> service.removerEstado(regiaoId, estadoId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void moverEstadoParaCima_deveReordenarSomenteDentroDaMesmaRegiao() {
        UUID regiaoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        RegiaoIntergrupo regiao = novaRegiao();
        setId(regiao, regiaoId);
        EstadoIntergrupo primeiro = new EstadoIntergrupo(regiao, UnidadeFederativa.RJ, 0);
        EstadoIntergrupo segundo = new EstadoIntergrupo(regiao, UnidadeFederativa.SP, 1);
        setId(segundo, id);
        when(estadoRepository.findById(id)).thenReturn(Optional.of(segundo));
        when(estadoRepository.findAllByRegiaoIdOrderByOrdemAsc(regiaoId)).thenReturn(List.of(primeiro, segundo));
        EstadoIntergrupoResponse resultado = service.moverEstadoParaCima(regiaoId, id);
        assertThat(resultado.ordem()).isEqualTo(0);
        assertThat(primeiro.getOrdem()).isEqualTo(1);
        verify(estadoRepository).findAllByRegiaoIdOrderByOrdemAsc(regiaoId);
    }

    @Test
    void criarGrupo_deveSalvarComCamposNormalizadosDiasSemanaETelefones() {
        UUID estadoId = UUID.randomUUID();
        EstadoIntergrupo estado = novoEstado(novaRegiao());
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estado));
        when(grupoRepository.findAllByEstadoIdOrderByOrdemAsc(estadoId)).thenReturn(List.of());
        when(grupoRepository.save(any(GrupoIntergrupo.class))).thenAnswer(inv -> inv.getArgument(0));
        CriarGrupoRequest request = new CriarGrupoRequest("Grupo Pioneiro", "Rua X", "  ", "São Paulo - SP",
                List.of(new AgendaItemRequest("SEGUNDA", "18h"), new AgendaItemRequest("QUINTA", "18h")),
                List.of(new GrupoTelefoneRequest(null, "11 90000-0000"),
                        new GrupoTelefoneRequest("Central", "11 91111-1111")));
        GrupoIntergrupoResponse resultado = service.criarGrupo(estadoId, request);
        assertThat(resultado.nome()).isEqualTo("Grupo Pioneiro");
        assertThat(resultado.bairro()).isNull();
        assertThat(resultado.agenda()).extracting("diaSemana").containsExactlyInAnyOrder("SEGUNDA", "QUINTA");
        assertThat(resultado.agenda()).extracting("horario").containsOnly("18h");
        assertThat(resultado.telefones()).hasSize(2);
        assertThat(resultado.telefones()).extracting("telefone")
                .containsExactlyInAnyOrder("11 90000-0000", "11 91111-1111");
    }

    @Test
    void criarGrupo_deveLancarExcecaoQuandoDiaSemanaInvalido() {
        UUID estadoId = UUID.randomUUID();
        EstadoIntergrupo estado = novoEstado(novaRegiao());
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estado));
        CriarGrupoRequest request = new CriarGrupoRequest("Grupo Pioneiro", "Rua X", null, "São Paulo - SP",
                List.of(new AgendaItemRequest("FERIADO", "18h")), List.of());
        assertThatThrownBy(() -> service.criarGrupo(estadoId, request))
                .isInstanceOf(ValidationException.class);
        verify(grupoRepository, never()).save(any());
    }

    @Test
    void criarGrupo_devePermitirListaDeTelefonesVazia() {
        UUID estadoId = UUID.randomUUID();
        EstadoIntergrupo estado = novoEstado(novaRegiao());
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estado));
        when(grupoRepository.findAllByEstadoIdOrderByOrdemAsc(estadoId)).thenReturn(List.of());
        when(grupoRepository.save(any(GrupoIntergrupo.class))).thenAnswer(inv -> inv.getArgument(0));
        CriarGrupoRequest request = new CriarGrupoRequest("Grupo Sem Telefone", "Rua X", null, "Cidade",
                List.of(new AgendaItemRequest("DOMINGO", "9h")), List.of());
        GrupoIntergrupoResponse resultado = service.criarGrupo(estadoId, request);
        assertThat(resultado.telefones()).isEmpty();
    }

    @Test
    void editarGrupo_deveSubstituirDiasEHorarioETelefones() {
        UUID estadoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        EstadoIntergrupo estado = novoEstado(novaRegiao());
        setId(estado, estadoId);
        GrupoIntergrupo grupo = novoGrupo(estado);
        setId(grupo, id);
        when(grupoRepository.findById(id)).thenReturn(Optional.of(grupo));
        EditarGrupoRequest request = new EditarGrupoRequest("Grupo Editado", "Rua Nova", "Bairro", "Cidade",
                List.of(new AgendaItemRequest("TERCA", "19h"), new AgendaItemRequest("SEXTA", "19h"),
                        new AgendaItemRequest("SABADO", "19h")),
                List.of(new GrupoTelefoneRequest("Emergência", "11 92222-2222")));
        GrupoIntergrupoResponse resultado = service.editarGrupo(estadoId, id, request);
        assertThat(resultado.nome()).isEqualTo("Grupo Editado");
        assertThat(resultado.agenda()).extracting("diaSemana")
                .containsExactlyInAnyOrder("TERCA", "SEXTA", "SABADO");
        assertThat(resultado.agenda()).extracting("horario").containsOnly("19h");
        assertThat(resultado.telefones()).hasSize(1);
        assertThat(resultado.telefones().get(0).telefone()).isEqualTo("11 92222-2222");
    }

    @Test
    void removerGrupo_deveLancarExcecaoQuandoGrupoNaoPertenceAoEstado() {
        UUID estadoId = UUID.randomUUID();
        UUID outroEstadoId = UUID.randomUUID();
        UUID grupoId = UUID.randomUUID();
        EstadoIntergrupo outroEstado = novoEstado(novaRegiao());
        setId(outroEstado, outroEstadoId);
        GrupoIntergrupo grupo = novoGrupo(outroEstado);
        when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
        assertThatThrownBy(() -> service.removerGrupo(estadoId, grupoId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void moverGrupoParaBaixo_deveReordenarSomenteDentroDoMesmoEstado() {
        UUID estadoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        EstadoIntergrupo estado = novoEstado(novaRegiao());
        setId(estado, estadoId);
        GrupoIntergrupo primeiro = new GrupoIntergrupo(estado, "Grupo 1", "Rua", null, "Cidade",
                List.of(new AgendaItemGrupo(DiaSemana.SEGUNDA, "18h")), 0);
        setId(primeiro, id);
        GrupoIntergrupo segundo = new GrupoIntergrupo(estado, "Grupo 2", "Rua", null, "Cidade",
                List.of(new AgendaItemGrupo(DiaSemana.TERCA, "19h")), 1);
        when(grupoRepository.findById(id)).thenReturn(Optional.of(primeiro));
        when(grupoRepository.findAllByEstadoIdOrderByOrdemAsc(estadoId)).thenReturn(List.of(primeiro, segundo));
        GrupoIntergrupoResponse resultado = service.moverGrupoParaBaixo(estadoId, id);
        assertThat(resultado.ordem()).isEqualTo(1);
        assertThat(segundo.getOrdem()).isEqualTo(0);
        verify(grupoRepository).findAllByEstadoIdOrderByOrdemAsc(estadoId);
    }

    @Test
    void criarEscritorioServico_deveSalvarComListaDeEmailsSemAceitarOrdemDoPayload() {
        when(escritorioServicoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of());
        when(escritorioServicoRepository.save(any(EscritorioServico.class))).thenAnswer(inv -> inv.getArgument(0));
        CriarEscritorioServicoRequest request = new CriarEscritorioServicoRequest("ERSJAB", "Rua Y", "Sala 12", null,
                "São Paulo - SP", List.of("a@a.com", "b@b.com"), List.of());
        EscritorioServicoResponse resultado = service.criarEscritorioServico(request);
        assertThat(resultado.emails()).containsExactly("a@a.com", "b@b.com");
        assertThat(resultado.ordem()).isEqualTo(0);
    }

    @Test
    void criarEscritorioServico_deveSalvarComBairroEMultiplosTelefones() {
        when(escritorioServicoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of());
        when(escritorioServicoRepository.save(any(EscritorioServico.class))).thenAnswer(inv -> inv.getArgument(0));
        CriarEscritorioServicoRequest request = new CriarEscritorioServicoRequest("ERSJAB", "Rua Y", "Sala 12",
                "Centro", "São Paulo - SP", List.of("a@a.com"),
                List.of(new EscritorioTelefoneRequest(null, "11 90000-0000"),
                        new EscritorioTelefoneRequest("Central", "11 91111-1111")));
        EscritorioServicoResponse resultado = service.criarEscritorioServico(request);
        assertThat(resultado.bairro()).isEqualTo("Centro");
        assertThat(resultado.telefones()).hasSize(2);
        assertThat(resultado.telefones()).extracting("telefone")
                .containsExactlyInAnyOrder("11 90000-0000", "11 91111-1111");
    }

    @Test
    void criarEscritorioServico_devePermitirBairroNuloEListaDeTelefonesVazia() {
        when(escritorioServicoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of());
        when(escritorioServicoRepository.save(any(EscritorioServico.class))).thenAnswer(inv -> inv.getArgument(0));
        CriarEscritorioServicoRequest request = new CriarEscritorioServicoRequest("ERSJAB", "Rua Y", "Sala 12", null,
                "São Paulo - SP", List.of("a@a.com"), List.of());
        EscritorioServicoResponse resultado = service.criarEscritorioServico(request);
        assertThat(resultado.bairro()).isNull();
        assertThat(resultado.telefones()).isEmpty();
    }

    @Test
    void editarEscritorioServico_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(escritorioServicoRepository.findById(id)).thenReturn(Optional.empty());
        EditarEscritorioServicoRequest request = new EditarEscritorioServicoRequest("nome", "endereco", null, null,
                "cidade", List.of(), List.of());
        assertThatThrownBy(() -> service.editarEscritorioServico(id, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void editarEscritorioServico_deveSubstituirBairroEAdicionarNovosTelefonesRemovendoAntigos() {
        UUID id = UUID.randomUUID();
        EscritorioServico escritorio = new EscritorioServico("ERSJAB", "Rua Y", "Sala 12", "Bairro Antigo", "Cidade",
                List.of("a@a.com"), 0);
        escritorio.substituirTelefones(List.of(new EscritorioServicoTelefone(escritorio, "Antigo", "11 90000-0000")));
        setId(escritorio, id);
        when(escritorioServicoRepository.findById(id)).thenReturn(Optional.of(escritorio));
        EditarEscritorioServicoRequest request = new EditarEscritorioServicoRequest("ERSJAB", "Rua Y", "Sala 12",
                "Bairro Novo", "Cidade", List.of("a@a.com"),
                List.of(new EscritorioTelefoneRequest("Emergência", "11 92222-2222"),
                        new EscritorioTelefoneRequest("Suporte", "11 93333-3333")));
        EscritorioServicoResponse resultado = service.editarEscritorioServico(id, request);
        assertThat(resultado.bairro()).isEqualTo("Bairro Novo");
        assertThat(resultado.telefones()).hasSize(2);
        assertThat(resultado.telefones()).extracting("telefone")
                .containsExactlyInAnyOrder("11 92222-2222", "11 93333-3333");
        assertThat(resultado.telefones()).extracting("telefone")
                .doesNotContain("11 90000-0000");
    }

    @Test
    void editarEscritorioServico_deveEsvaziarBairroETelefonesQuandoOmitidos() {
        UUID id = UUID.randomUUID();
        EscritorioServico escritorio = new EscritorioServico("ERSJAB", "Rua Y", "Sala 12", "Bairro Antigo", "Cidade",
                List.of("a@a.com"), 0);
        escritorio.substituirTelefones(List.of(new EscritorioServicoTelefone(escritorio, "Antigo", "11 90000-0000")));
        setId(escritorio, id);
        when(escritorioServicoRepository.findById(id)).thenReturn(Optional.of(escritorio));
        EditarEscritorioServicoRequest request = new EditarEscritorioServicoRequest("ERSJAB", "Rua Y", "Sala 12",
                null, "Cidade", List.of("a@a.com"), List.of());
        EscritorioServicoResponse resultado = service.editarEscritorioServico(id, request);
        assertThat(resultado.bairro()).isNull();
        assertThat(resultado.telefones()).isEmpty();
    }

    @Test
    void removerEscritorioServico_deveDeletarQuandoExiste() {
        UUID id = UUID.randomUUID();
        EscritorioServico escritorio = new EscritorioServico("nome", "endereco", null, null, "cidade", List.of(), 0);
        when(escritorioServicoRepository.findById(id)).thenReturn(Optional.of(escritorio));
        service.removerEscritorioServico(id);
        verify(escritorioServicoRepository).delete(escritorio);
    }

    @Test
    void moverEscritorioServicoParaCima_deveSerNoOpQuandoJaEhPrimeiro() {
        UUID id = UUID.randomUUID();
        EscritorioServico primeiro = new EscritorioServico("A", "Rua", null, null, "Cidade", List.of(), 0);
        setId(primeiro, id);
        EscritorioServico segundo = new EscritorioServico("B", "Rua", null, null, "Cidade", List.of(), 1);
        when(escritorioServicoRepository.findById(id)).thenReturn(Optional.of(primeiro));
        when(escritorioServicoRepository.findAllByOrderByOrdemAsc()).thenReturn(List.of(primeiro, segundo));
        EscritorioServicoResponse resultado = service.moverEscritorioServicoParaCima(id);
        assertThat(resultado.ordem()).isEqualTo(0);
        assertThat(segundo.getOrdem()).isEqualTo(1);
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

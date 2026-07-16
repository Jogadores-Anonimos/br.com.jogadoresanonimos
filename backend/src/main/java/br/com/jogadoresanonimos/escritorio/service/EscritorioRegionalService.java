package br.com.jogadoresanonimos.escritorio.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.util.ReordenacaoUtil;
import br.com.jogadoresanonimos.escritorio.dto.*;
import br.com.jogadoresanonimos.escritorio.entity.*;
import br.com.jogadoresanonimos.escritorio.repository.EscritorioServicoRepository;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.GrupoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.RegiaoIntergrupoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EscritorioRegionalService {

    private final RegiaoIntergrupoRepository regiaoRepository;
    private final EstadoIntergrupoRepository estadoRepository;
    private final GrupoIntergrupoRepository grupoRepository;
    private final EscritorioServicoRepository escritorioServicoRepository;

    public EscritorioRegionalService(RegiaoIntergrupoRepository regiaoRepository,
                                      EstadoIntergrupoRepository estadoRepository,
                                      GrupoIntergrupoRepository grupoRepository,
                                      EscritorioServicoRepository escritorioServicoRepository) {
        this.regiaoRepository = regiaoRepository;
        this.estadoRepository = estadoRepository;
        this.grupoRepository = grupoRepository;
        this.escritorioServicoRepository = escritorioServicoRepository;
    }

    @Transactional(readOnly = true)
    public EscritoriosRegionaisResponse listarEstruturaCompleta() {
        var escritorios = escritorioServicoRepository.findAllByOrderByOrdemAsc().stream()
                .map(EscritorioServicoResponse::from).toList();
        List<RegiaoIntergrupo> regioes = regiaoRepository.findAllComEstados();
        if (!regioes.isEmpty()) {
            List<EstadoIntergrupo> estados = regiaoRepository.carregarGruposDosEstados(regioes);
            if (!estados.isEmpty()) {
                regiaoRepository.carregarTelefonesDosGrupos(estados);
            }
        }
        var regioesResponse = regioes.stream().map(RegiaoIntergrupoResponse::from).toList();
        return new EscritoriosRegionaisResponse(escritorios, regioesResponse);
    }

    @Transactional
    public RegiaoIntergrupoResponse editarRegiao(UUID id, EditarRegiaoRequest request) {
        RegiaoIntergrupo regiao = buscarRegiaoOuFalhar(id);
        String nome = request.nome().trim();
        if (!nome.equalsIgnoreCase(regiao.getNome()) && regiaoRepository.existsByNomeIgnoreCase(nome)) {
            throw new ValidationException("Já existe uma região com esse nome.");
        }
        regiao.editar(nome);
        return RegiaoIntergrupoResponse.from(regiao);
    }

    @Transactional
    public RegiaoIntergrupoResponse moverRegiaoParaCima(UUID id) {
        return reordenarRegiao(id, true);
    }

    @Transactional
    public RegiaoIntergrupoResponse moverRegiaoParaBaixo(UUID id) {
        return reordenarRegiao(id, false);
    }

    private RegiaoIntergrupoResponse reordenarRegiao(UUID id, boolean paraCima) {
        RegiaoIntergrupo regiao = buscarRegiaoOuFalhar(id);
        List<RegiaoIntergrupo> ordenadas = regiaoRepository.findAllByOrderByOrdemAsc();
        if (paraCima) {
            ReordenacaoUtil.moverParaCima(ordenadas, regiao, RegiaoIntergrupo::getOrdem, RegiaoIntergrupo::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenadas, regiao, RegiaoIntergrupo::getOrdem, RegiaoIntergrupo::reordenar);
        }
        return RegiaoIntergrupoResponse.semEstados(regiao);
    }

    @Transactional
    public EstadoIntergrupoResponse criarEstado(UUID regiaoId, CriarEstadoRequest request) {
        RegiaoIntergrupo regiao = buscarRegiaoOuFalhar(regiaoId);
        UnidadeFederativa uf = resolverUf(request.uf());
        if (estadoRepository.existsByRegiaoIdAndUf(regiaoId, uf)) {
            throw new ConflitoDeEstadoException(
                    "Já existe um estado com essa UF nesta região. Edite o estado existente em vez de criar um novo.");
        }
        int ordem = proximaOrdem(estadoRepository.findAllByRegiaoIdOrderByOrdemAsc(regiaoId), EstadoIntergrupo::getOrdem);
        EstadoIntergrupo estado = new EstadoIntergrupo(regiao, uf, ordem);
        estado = estadoRepository.save(estado);
        return EstadoIntergrupoResponse.semGrupos(estado);
    }

    @Transactional
    public EstadoIntergrupoResponse editarEstado(UUID regiaoId, UUID id, EditarEstadoRequest request) {
        EstadoIntergrupo estado = buscarEstadoOuFalhar(regiaoId, id);
        UnidadeFederativa uf = resolverUf(request.uf());
        if (uf != estado.getUf() && estadoRepository.existsByRegiaoIdAndUf(regiaoId, uf)) {
            throw new ConflitoDeEstadoException(
                    "Já existe um estado com essa UF nesta região. Edite o estado existente em vez de criar um novo.");
        }
        estado.editar(uf);
        return EstadoIntergrupoResponse.from(estado);
    }

    @Transactional
    public void removerEstado(UUID regiaoId, UUID id) {
        EstadoIntergrupo estado = buscarEstadoOuFalhar(regiaoId, id);
        estadoRepository.delete(estado);
    }

    @Transactional
    public EstadoIntergrupoResponse moverEstadoParaCima(UUID regiaoId, UUID id) {
        return reordenarEstado(regiaoId, id, true);
    }

    @Transactional
    public EstadoIntergrupoResponse moverEstadoParaBaixo(UUID regiaoId, UUID id) {
        return reordenarEstado(regiaoId, id, false);
    }

    private EstadoIntergrupoResponse reordenarEstado(UUID regiaoId, UUID id, boolean paraCima) {
        EstadoIntergrupo estado = buscarEstadoOuFalhar(regiaoId, id);
        List<EstadoIntergrupo> ordenados = estadoRepository.findAllByRegiaoIdOrderByOrdemAsc(regiaoId);
        if (paraCima) {
            ReordenacaoUtil.moverParaCima(ordenados, estado, EstadoIntergrupo::getOrdem, EstadoIntergrupo::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenados, estado, EstadoIntergrupo::getOrdem, EstadoIntergrupo::reordenar);
        }
        return EstadoIntergrupoResponse.from(estado);
    }

    @Transactional
    public GrupoIntergrupoResponse criarGrupo(UUID estadoId, CriarGrupoRequest request) {
        EstadoIntergrupo estado = buscarEstadoOuFalhar(estadoId);
        return criarGrupoNoEstado(estado, estadoId, request.nome(), request.endereco(), request.bairro(),
                request.cidade(), request.agenda(), request.telefones());
    }

    @Transactional
    public GrupoIntergrupoResponse criarEnderecoGrupo(UUID regiaoId, CriarEnderecoGrupoRequest request) {
        RegiaoIntergrupo regiao = buscarRegiaoOuFalhar(regiaoId);
        UnidadeFederativa uf = resolverUf(request.uf());
        EstadoIntergrupo estado = estadoRepository.findByRegiaoIdAndUf(regiaoId, uf)
                .orElseGet(() -> criarEstadoAutomaticamente(regiao, uf));
        return criarGrupoNoEstado(estado, estado.getId(), request.nome(), request.endereco(), request.bairro(),
                request.cidade(), request.agenda(), request.telefones());
    }

    private EstadoIntergrupo criarEstadoAutomaticamente(RegiaoIntergrupo regiao, UnidadeFederativa uf) {
        int ordem = proximaOrdem(estadoRepository.findAllByRegiaoIdOrderByOrdemAsc(regiao.getId()), EstadoIntergrupo::getOrdem);
        return estadoRepository.save(new EstadoIntergrupo(regiao, uf, ordem));
    }

    private GrupoIntergrupoResponse criarGrupoNoEstado(EstadoIntergrupo estado, UUID estadoId, String nome,
                                                         String endereco, String bairro, String cidade,
                                                         List<AgendaItemRequest> agendaRequest,
                                                         List<GrupoTelefoneRequest> telefonesRequest) {
        List<AgendaItemGrupo> agenda = resolverAgenda(agendaRequest);
        int ordem = proximaOrdem(grupoRepository.findAllByEstadoIdOrderByOrdemAsc(estadoId), GrupoIntergrupo::getOrdem);
        GrupoIntergrupo grupo = new GrupoIntergrupo(estado, nome.trim(), endereco.trim(),
                normalizar(bairro), cidade.trim(), agenda, ordem);
        grupo = grupoRepository.save(grupo);
        grupo.substituirTelefones(criarTelefones(grupo, telefonesRequest));
        return GrupoIntergrupoResponse.from(grupo);
    }

    @Transactional
    public GrupoIntergrupoResponse editarGrupo(UUID estadoId, UUID id, EditarGrupoRequest request) {
        GrupoIntergrupo grupo = buscarGrupoOuFalhar(estadoId, id);
        List<AgendaItemGrupo> agenda = resolverAgenda(request.agenda());
        grupo.editar(request.nome().trim(), request.endereco().trim(), normalizar(request.bairro()),
                request.cidade().trim(), agenda);
        grupo.substituirTelefones(criarTelefones(grupo, request.telefones()));
        return GrupoIntergrupoResponse.from(grupo);
    }

    @Transactional
    public void removerGrupo(UUID estadoId, UUID id) {
        GrupoIntergrupo grupo = buscarGrupoOuFalhar(estadoId, id);
        grupoRepository.delete(grupo);
    }

    @Transactional
    public GrupoIntergrupoResponse moverGrupoParaCima(UUID estadoId, UUID id) {
        return reordenarGrupo(estadoId, id, true);
    }

    @Transactional
    public GrupoIntergrupoResponse moverGrupoParaBaixo(UUID estadoId, UUID id) {
        return reordenarGrupo(estadoId, id, false);
    }

    private GrupoIntergrupoResponse reordenarGrupo(UUID estadoId, UUID id, boolean paraCima) {
        GrupoIntergrupo grupo = buscarGrupoOuFalhar(estadoId, id);
        List<GrupoIntergrupo> ordenados = grupoRepository.findAllByEstadoIdOrderByOrdemAsc(estadoId);
        if (paraCima) {
            ReordenacaoUtil.moverParaCima(ordenados, grupo, GrupoIntergrupo::getOrdem, GrupoIntergrupo::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenados, grupo, GrupoIntergrupo::getOrdem, GrupoIntergrupo::reordenar);
        }
        return GrupoIntergrupoResponse.from(grupo);
    }

    private List<GrupoIntergrupoTelefone> criarTelefones(GrupoIntergrupo grupo, List<GrupoTelefoneRequest> telefones) {
        if (telefones == null) {
            return List.of();
        }
        return telefones.stream()
                .map(t -> new GrupoIntergrupoTelefone(grupo, normalizar(t.label()), t.telefone().trim()))
                .toList();
    }

    private List<EscritorioServicoTelefone> criarTelefones(EscritorioServico escritorio,
                                                             List<EscritorioTelefoneRequest> telefones) {
        if (telefones == null) {
            return List.of();
        }
        return telefones.stream()
                .map(t -> new EscritorioServicoTelefone(escritorio, normalizar(t.label()), t.telefone().trim()))
                .toList();
    }

    @Transactional
    public EscritorioServicoResponse criarEscritorioServico(CriarEscritorioServicoRequest request) {
        int ordem = proximaOrdem(escritorioServicoRepository.findAllByOrderByOrdemAsc(), EscritorioServico::getOrdem);
        EscritorioServico escritorio = new EscritorioServico(request.nome().trim(), request.endereco().trim(),
                normalizar(request.complemento()), normalizar(request.bairro()), request.cidade().trim(),
                request.emails(), ordem);
        escritorio = escritorioServicoRepository.save(escritorio);
        escritorio.substituirTelefones(criarTelefones(escritorio, request.telefones()));
        return EscritorioServicoResponse.from(escritorio);
    }

    @Transactional
    public EscritorioServicoResponse editarEscritorioServico(UUID id, EditarEscritorioServicoRequest request) {
        EscritorioServico escritorio = buscarEscritorioServicoOuFalhar(id);
        escritorio.editar(request.nome().trim(), request.endereco().trim(), normalizar(request.complemento()),
                normalizar(request.bairro()), request.cidade().trim(), request.emails());
        escritorio.substituirTelefones(criarTelefones(escritorio, request.telefones()));
        return EscritorioServicoResponse.from(escritorio);
    }

    @Transactional
    public void removerEscritorioServico(UUID id) {
        EscritorioServico escritorio = buscarEscritorioServicoOuFalhar(id);
        escritorioServicoRepository.delete(escritorio);
    }

    @Transactional
    public EscritorioServicoResponse moverEscritorioServicoParaCima(UUID id) {
        return reordenarEscritorioServico(id, true);
    }

    @Transactional
    public EscritorioServicoResponse moverEscritorioServicoParaBaixo(UUID id) {
        return reordenarEscritorioServico(id, false);
    }

    private EscritorioServicoResponse reordenarEscritorioServico(UUID id, boolean paraCima) {
        EscritorioServico escritorio = buscarEscritorioServicoOuFalhar(id);
        List<EscritorioServico> ordenados = escritorioServicoRepository.findAllByOrderByOrdemAsc();
        if (paraCima) {
            ReordenacaoUtil.moverParaCima(ordenados, escritorio, EscritorioServico::getOrdem, EscritorioServico::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenados, escritorio, EscritorioServico::getOrdem, EscritorioServico::reordenar);
        }
        return EscritorioServicoResponse.from(escritorio);
    }

    private RegiaoIntergrupo buscarRegiaoOuFalhar(UUID id) {
        return regiaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada"));
    }

    private EstadoIntergrupo buscarEstadoOuFalhar(UUID id) {
        return estadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estado não encontrado"));
    }

    private EstadoIntergrupo buscarEstadoOuFalhar(UUID regiaoId, UUID id) {
        EstadoIntergrupo estado = buscarEstadoOuFalhar(id);
        if (!estado.getRegiao().getId().equals(regiaoId)) {
            throw new RecursoNaoEncontradoException("Estado não encontrado nesta região");
        }
        return estado;
    }

    private GrupoIntergrupo buscarGrupoOuFalhar(UUID estadoId, UUID id) {
        GrupoIntergrupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo não encontrado"));
        if (!grupo.getEstado().getId().equals(estadoId)) {
            throw new RecursoNaoEncontradoException("Grupo não encontrado neste estado");
        }
        return grupo;
    }

    private EscritorioServico buscarEscritorioServicoOuFalhar(UUID id) {
        return escritorioServicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Escritório de serviço não encontrado"));
    }

    private static UnidadeFederativa resolverUf(String uf) {
        return UnidadeFederativa.deSigla(uf)
                .orElseThrow(() -> new ValidationException("UF inválida: informe uma das 27 UFs do Brasil."));
    }

    private static List<AgendaItemGrupo> resolverAgenda(List<AgendaItemRequest> agenda) {
        Set<DiaSemana> diasJaVistos = new HashSet<>();
        List<AgendaItemGrupo> itens = new ArrayList<>();
        for (AgendaItemRequest item : agenda) {
            DiaSemana dia;
            try {
                dia = DiaSemana.valueOf(item.diaSemana().trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ValidationException("Dia da semana inválido: " + item.diaSemana());
            }
            if (!diasJaVistos.add(dia)) {
                throw new ValidationException("Dia da semana duplicado na agenda: " + dia.name());
            }
            itens.add(new AgendaItemGrupo(dia, item.horario().trim()));
        }
        return itens;
    }

    private static <T> int proximaOrdem(List<T> itensOrdenados, java.util.function.ToIntFunction<T> getOrdem) {
        return itensOrdenados.stream().mapToInt(getOrdem).max().orElse(-1) + 1;
    }

    private static String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String trimado = valor.trim();
        return trimado.isEmpty() ? null : trimado;
    }
}

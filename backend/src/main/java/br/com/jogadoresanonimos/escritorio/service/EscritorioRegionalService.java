package br.com.jogadoresanonimos.escritorio.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.util.ReordenacaoUtil;
import br.com.jogadoresanonimos.escritorio.dto.*;
import br.com.jogadoresanonimos.escritorio.entity.*;
import br.com.jogadoresanonimos.escritorio.repository.EscritorioServicoRepository;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.GrupoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.RegiaoIntergrupoRepository;
import br.com.jogadoresanonimos.reuniao.service.ReuniaoGrupoSincronizacaoService;
import br.com.jogadoresanonimos.reuniao.service.DadosEnderecoReuniao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EscritorioRegionalService {

    private final RegiaoIntergrupoRepository regiaoRepository;
    private final EstadoIntergrupoRepository estadoRepository;
    private final GrupoIntergrupoRepository grupoRepository;
    private final EscritorioServicoRepository escritorioServicoRepository;
    private final ReuniaoGrupoSincronizacaoService sincronizacaoService;

    public EscritorioRegionalService(RegiaoIntergrupoRepository regiaoRepository,
                                      EstadoIntergrupoRepository estadoRepository,
                                      GrupoIntergrupoRepository grupoRepository,
                                      EscritorioServicoRepository escritorioServicoRepository) {
        this(regiaoRepository, estadoRepository, grupoRepository, escritorioServicoRepository, null);
    }

    @Autowired
    public EscritorioRegionalService(RegiaoIntergrupoRepository regiaoRepository,
                                      EstadoIntergrupoRepository estadoRepository,
                                      GrupoIntergrupoRepository grupoRepository,
                                      EscritorioServicoRepository escritorioServicoRepository,
                                      ReuniaoGrupoSincronizacaoService sincronizacaoService) {
        this.regiaoRepository = regiaoRepository;
        this.estadoRepository = estadoRepository;
        this.grupoRepository = grupoRepository;
        this.escritorioServicoRepository = escritorioServicoRepository;
        this.sincronizacaoService = sincronizacaoService;
    }

    public List<UnidadeFederativaResponse> listarUnidadesFederativas() {
        return Arrays.stream(UnidadeFederativa.values()).map(UnidadeFederativaResponse::from).toList();
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
            throw new ValidationException(ErrorCode.ESCRITORIO_REGIAO_NOME_EM_USO, "Já existe uma região com esse nome.");
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
            ReordenacaoUtil.moverParaCima(ordenadas, regiao, this::buscarRegiaoComLock, RegiaoIntergrupo::getId, RegiaoIntergrupo::getOrdem, RegiaoIntergrupo::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenadas, regiao, this::buscarRegiaoComLock, RegiaoIntergrupo::getId, RegiaoIntergrupo::getOrdem, RegiaoIntergrupo::reordenar);
        }
        return RegiaoIntergrupoResponse.semEstados(regiao);
    }

    @Transactional
    public EstadoIntergrupoResponse criarEstado(UUID regiaoId, CriarEstadoRequest request) {
        RegiaoIntergrupo regiao = buscarRegiaoOuFalhar(regiaoId);
        UnidadeFederativa uf = resolverUf(request.uf());
        if (estadoRepository.existsByRegiaoIdAndUf(regiaoId, uf)) {
            throw new ConflitoDeEstadoException(ErrorCode.ESCRITORIO_ESTADO_UF_EM_USO,
                    "Já existe um estado com essa UF nesta região. Edite o estado existente em vez de criar um novo.");
        }
        int ordem = proximaOrdem(estadoRepository.findAllByRegiaoIdOrderByOrdemAsc(regiaoId), EstadoIntergrupo::getOrdem);
        EstadoIntergrupo estado = new EstadoIntergrupo(regiao, uf, ordem);
        estado = estadoRepository.save(estado);
        return EstadoIntergrupoResponse.semGrupos(estado);
    }

    @Transactional
    public EstadoIntergrupoResponse editarEstado(UUID regiaoId, UUID id, EditarEstadoRequest request) {
        EstadoIntergrupo estado = buscarEstadoParaEscrita(regiaoId, id);
        UnidadeFederativa uf = resolverUf(request.uf());
        if (uf != estado.getUf() && sincronizacaoService != null && sincronizacaoService.possuiVinculoNoEstado(id)) {
            throw new ConflitoDeEstadoException(ErrorCode.REUNIAO_GRUPO_UF_DIVERGENTE,
                    "Não é possível alterar a UF de um estado com reuniões vinculadas");
        }
        if (uf != estado.getUf() && estadoRepository.existsByRegiaoIdAndUf(regiaoId, uf)) {
            throw new ConflitoDeEstadoException(ErrorCode.ESCRITORIO_ESTADO_UF_EM_USO,
                    "Já existe um estado com essa UF nesta região. Edite o estado existente em vez de criar um novo.");
        }
        estado.editar(uf);
        return EstadoIntergrupoResponse.from(estado);
    }

    @Transactional
    public void removerEstado(UUID regiaoId, UUID id) {
        EstadoIntergrupo estado = buscarEstadoParaEscrita(regiaoId, id);
        if (sincronizacaoService != null && sincronizacaoService.possuiVinculoNoEstado(id)) {
            throw new ConflitoDeEstadoException(ErrorCode.REUNIAO_GRUPO_CONFLITO,
                    "Estado possui reuniões vinculadas; remova ou mova os grupos antes de excluir");
        }
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
            ReordenacaoUtil.moverParaCima(ordenados, estado, this::buscarEstadoComLock, EstadoIntergrupo::getId, EstadoIntergrupo::getOrdem, EstadoIntergrupo::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenados, estado, this::buscarEstadoComLock, EstadoIntergrupo::getId, EstadoIntergrupo::getOrdem, EstadoIntergrupo::reordenar);
        }
        return EstadoIntergrupoResponse.from(estado);
    }

    @Transactional
    public GrupoIntergrupoResponse criarGrupo(UUID estadoId, CriarGrupoRequest request) {
        EstadoIntergrupo estado = buscarEstadoParaEscrita(estadoId);
        validarUfSolicitada(request.estado(), estado);
        return criarGrupoNoEstado(estado, estadoId, request.nome(), request.endereco(), request.bairro(),
                request.cidade(), request.agenda(), request.telefones(),
                dadosEndereco(request.cep(), request.numero(), request.logradouro(), request.bairro(),
                        request.cidade(), estado.getUf().name(), request.latitude(), request.longitude()));
    }

    @Transactional
    public GrupoIntergrupoResponse criarEnderecoGrupo(UUID regiaoId, CriarEnderecoGrupoRequest request) {
        RegiaoIntergrupo regiao = sincronizacaoService == null
                ? buscarRegiaoOuFalhar(regiaoId) : buscarRegiaoComLock(regiaoId);
        UnidadeFederativa uf = resolverUf(request.uf());
        EstadoIntergrupo estado = estadoRepository.findByRegiaoIdAndUf(regiaoId, uf)
                .map(encontrado -> buscarEstadoParaEscrita(encontrado.getId()))
                .orElseGet(() -> criarEstadoAutomaticamente(regiao, uf));
        return criarGrupoNoEstado(estado, estado.getId(), request.nome(), request.endereco(), request.bairro(),
                request.cidade(), request.agenda(), request.telefones(),
                dadosEndereco(request.cep(), request.numero(), request.logradouro(), request.bairro(),
                        request.cidade(), estado.getUf().name(), request.latitude(), request.longitude()));
    }

    private EstadoIntergrupo criarEstadoAutomaticamente(RegiaoIntergrupo regiao, UnidadeFederativa uf) {
        int ordem = proximaOrdem(estadoRepository.findAllByRegiaoIdOrderByOrdemAsc(regiao.getId()), EstadoIntergrupo::getOrdem);
        return estadoRepository.save(new EstadoIntergrupo(regiao, uf, ordem));
    }

    private GrupoIntergrupoResponse criarGrupoNoEstado(EstadoIntergrupo estado, UUID estadoId, String nome,
                                                         String endereco, String bairro, String cidade,
                                                         List<AgendaItemRequest> agendaRequest,
                                                         List<GrupoTelefoneRequest> telefonesRequest,
                                                         DadosEnderecoReuniao dadosEndereco) {
        List<AgendaItemGrupo> agenda = resolverAgenda(agendaRequest);
        int ordem = proximaOrdem(grupoRepository.findAllByEstadoIdOrderByOrdemAsc(estadoId), GrupoIntergrupo::getOrdem);
        GrupoIntergrupo grupo = new GrupoIntergrupo(estado, nome.trim(), endereco.trim(),
                normalizar(bairro), cidade.trim(), agenda, ordem);
        grupo = grupoRepository.save(grupo);
        grupo.substituirTelefones(criarTelefones(grupo, telefonesRequest));
        if (sincronizacaoService != null) {
            sincronizacaoService.criarReuniaoCanonica(grupo, dadosEndereco);
        }
        return GrupoIntergrupoResponse.from(grupo);
    }

    @Transactional
    public GrupoIntergrupoResponse editarGrupo(UUID estadoId, UUID id, EditarGrupoRequest request) {
        List<AgendaItemGrupo> agenda = resolverAgenda(request.agenda());
        GrupoIntergrupo grupoEscopo;
        if (sincronizacaoService == null) {
            grupoEscopo = buscarGrupoOuFalhar(estadoId, id);
        } else {
            EstadoIntergrupo estado = buscarEstadoComLock(estadoId);
            grupoEscopo = buscarGrupoComLock(id);
            validarGrupoNoEstado(grupoEscopo, estado.getId());
        }
        validarUfSolicitada(request.estado(), grupoEscopo.getEstado());
        DadosEnderecoReuniao dadosEndereco = dadosEndereco(request.cep(), request.numero(), request.logradouro(),
                request.bairro(), request.cidade(), grupoEscopo.getEstado().getUf().name(),
                request.latitude(), request.longitude());
        GrupoIntergrupo grupo;
        boolean sincronizado = false;
        if (sincronizacaoService == null) {
            grupo = buscarGrupoParaEscrita(estadoId, id);
        } else {
            var grupoSincronizado = sincronizacaoService.sincronizarEdicaoPorGrupo(id, request.nome().trim(),
                    request.endereco().trim(), dadosEndereco, agenda,
                    request.atualizadoEm());
            sincronizado = grupoSincronizado.isPresent();
            grupo = grupoSincronizado.orElseGet(() -> buscarGrupoParaEscrita(estadoId, id));
        }
        if (!sincronizado) {
            grupo.editar(request.nome().trim(), request.endereco().trim(), normalizar(request.bairro()),
                    request.cidade().trim(), agenda);
        }
        grupo.substituirTelefones(criarTelefones(grupo, request.telefones()));
        return GrupoIntergrupoResponse.from(grupo);
    }

    @Transactional
    public void removerGrupo(UUID estadoId, UUID id) {
        if (sincronizacaoService == null) {
            GrupoIntergrupo grupo = buscarGrupoParaEscrita(estadoId, id);
            grupoRepository.delete(grupo);
        } else {
            buscarEstadoComLock(estadoId);
            sincronizacaoService.removerPorGrupo(id, estadoId);
        }
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
            ReordenacaoUtil.moverParaCima(ordenados, grupo, this::buscarGrupoComLock, GrupoIntergrupo::getId, GrupoIntergrupo::getOrdem, GrupoIntergrupo::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenados, grupo, this::buscarGrupoComLock, GrupoIntergrupo::getId, GrupoIntergrupo::getOrdem, GrupoIntergrupo::reordenar);
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
            ReordenacaoUtil.moverParaCima(ordenados, escritorio, this::buscarEscritorioServicoComLock, EscritorioServico::getId, EscritorioServico::getOrdem, EscritorioServico::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenados, escritorio, this::buscarEscritorioServicoComLock, EscritorioServico::getId, EscritorioServico::getOrdem, EscritorioServico::reordenar);
        }
        return EscritorioServicoResponse.from(escritorio);
    }

    private RegiaoIntergrupo buscarRegiaoOuFalhar(UUID id) {
        return regiaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_REGIAO_NAO_ENCONTRADA, "Região não encontrada"));
    }

    private RegiaoIntergrupo buscarRegiaoComLock(UUID id) {
        return regiaoRepository.buscarPorIdComLock(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_REGIAO_NAO_ENCONTRADA, "Região não encontrada"));
    }

    private EstadoIntergrupo buscarEstadoOuFalhar(UUID id) {
        return estadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_ESTADO_NAO_ENCONTRADO, "Estado não encontrado"));
    }

    private EstadoIntergrupo buscarEstadoComLock(UUID id) {
        return estadoRepository.buscarPorIdComLock(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_ESTADO_NAO_ENCONTRADO, "Estado não encontrado"));
    }

    private EstadoIntergrupo buscarEstadoComLock(UUID regiaoId, UUID id) {
        EstadoIntergrupo estado = buscarEstadoComLock(id);
        if (!estado.getRegiao().getId().equals(regiaoId)) {
            throw new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_ESTADO_NAO_ENCONTRADO,
                    "Estado não encontrado nesta região");
        }
        return estado;
    }

    private EstadoIntergrupo buscarEstadoParaEscrita(UUID id) {
        return sincronizacaoService == null ? buscarEstadoOuFalhar(id) : buscarEstadoComLock(id);
    }

    private EstadoIntergrupo buscarEstadoParaEscrita(UUID regiaoId, UUID id) {
        EstadoIntergrupo estado = buscarEstadoParaEscrita(id);
        if (!estado.getRegiao().getId().equals(regiaoId)) {
            throw new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_ESTADO_NAO_ENCONTRADO,
                    "Estado não encontrado nesta região");
        }
        return estado;
    }

    private EstadoIntergrupo buscarEstadoOuFalhar(UUID regiaoId, UUID id) {
        EstadoIntergrupo estado = buscarEstadoOuFalhar(id);
        if (!estado.getRegiao().getId().equals(regiaoId)) {
            throw new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_ESTADO_NAO_ENCONTRADO, "Estado não encontrado nesta região");
        }
        return estado;
    }

    private GrupoIntergrupo buscarGrupoOuFalhar(UUID estadoId, UUID id) {
        GrupoIntergrupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_GRUPO_NAO_ENCONTRADO, "Grupo não encontrado"));
        if (!grupo.getEstado().getId().equals(estadoId)) {
            throw new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_GRUPO_NAO_ENCONTRADO, "Grupo não encontrado neste estado");
        }
        return grupo;
    }

    private GrupoIntergrupo buscarGrupoComLock(UUID id) {
        return grupoRepository.buscarPorIdComLock(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_GRUPO_NAO_ENCONTRADO, "Grupo não encontrado"));
    }

    private EscritorioServico buscarEscritorioServicoOuFalhar(UUID id) {
        return escritorioServicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_SERVICO_NAO_ENCONTRADO, "Escritório de serviço não encontrado"));
    }

    private EscritorioServico buscarEscritorioServicoComLock(UUID id) {
        return escritorioServicoRepository.buscarPorIdComLock(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_SERVICO_NAO_ENCONTRADO, "Escritório de serviço não encontrado"));
    }

    private static UnidadeFederativa resolverUf(String uf) {
        return UnidadeFederativa.deSigla(uf)
                .orElseThrow(() -> new ValidationException(ErrorCode.ESCRITORIO_UF_INVALIDA, "UF inválida: informe uma das 27 UFs do Brasil."));
    }

    private static List<AgendaItemGrupo> resolverAgenda(List<AgendaItemRequest> agenda) {
        if (agenda == null || agenda.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Agenda deve conter ao menos um horário");
        }
        if (agenda.size() > 336) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR,
                    "Agenda deve conter no máximo 336 horários");
        }
        Set<String> combinacoesJaVistas = new HashSet<>();
        List<AgendaItemGrupo> itens = new ArrayList<>();
        for (AgendaItemRequest item : agenda) {
            if (item == null) {
                throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Item da agenda é obrigatório");
            }
            DiaSemana dia;
            try {
                dia = DiaSemana.valueOf(item.diaSemana().trim().toUpperCase());
            } catch (IllegalArgumentException | NullPointerException ex) {
                throw new ValidationException(ErrorCode.ESCRITORIO_DIA_SEMANA_INVALIDO, "Dia da semana inválido",
                        null, Map.of("diaSemana", String.valueOf(item.diaSemana())));
            }
            String horario = item.horario() == null ? "" : item.horario().trim();
            if (horario.isEmpty()) {
                throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Horário é obrigatório");
            }
            if (horario.length() > 50) {
                throw new ValidationException(ErrorCode.VALIDATION_ERROR,
                        "Horário deve ter no máximo 50 caracteres");
            }
            if (!combinacoesJaVistas.add(dia.name() + '\u0000' + horario)) {
                throw new ValidationException(ErrorCode.ESCRITORIO_DIA_SEMANA_DUPLICADO, "Agenda não pode repetir o mesmo horário no mesmo dia",
                        null, Map.of("diaSemana", dia.name(), "horario", horario));
            }
            itens.add(new AgendaItemGrupo(dia, horario));
        }
        itens.sort(AgendaItemGrupoComparator.porDiaEHorario());
        return itens;
    }

    private void validarGrupoNoEstado(GrupoIntergrupo grupo, UUID estadoId) {
        if (!grupo.getEstado().getId().equals(estadoId)) {
            throw new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_GRUPO_NAO_ENCONTRADO,
                    "Grupo não encontrado neste estado");
        }
    }

    private GrupoIntergrupo buscarGrupoParaEscrita(UUID estadoId, UUID id) {
        GrupoIntergrupo grupo = sincronizacaoService == null ? buscarGrupoOuFalhar(estadoId, id)
                : buscarGrupoComLock(id);
        if (!grupo.getEstado().getId().equals(estadoId)) {
            throw new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_GRUPO_NAO_ENCONTRADO,
                    "Grupo não encontrado neste estado");
        }
        return grupo;
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

    private static DadosEnderecoReuniao dadosEndereco(String cep, String numero, String logradouro,
                                                        String bairro, String cidade, String estado,
                                                        Double latitude, Double longitude) {
        if ((latitude == null) != (longitude == null)) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR,
                    "Latitude e longitude devem ser informadas em conjunto");
        }
        boolean estruturaDetalhadaFornecida = cep != null || numero != null || logradouro != null
                || latitude != null || longitude != null;
        return new DadosEnderecoReuniao(normalizar(cep), normalizar(numero), normalizar(logradouro),
                normalizar(bairro), cidade.trim(), estado, latitude, longitude, estruturaDetalhadaFornecida);
    }

    private static void validarUfSolicitada(String ufSolicitada, EstadoIntergrupo estado) {
        String normalizada = normalizar(ufSolicitada);
        if (normalizada != null && !estado.getUf().name().equalsIgnoreCase(normalizada)) {
            throw new ConflitoDeEstadoException(ErrorCode.REUNIAO_GRUPO_UF_DIVERGENTE,
                    "A UF informada deve ser a mesma UF organizacional do estado");
        }
    }
}

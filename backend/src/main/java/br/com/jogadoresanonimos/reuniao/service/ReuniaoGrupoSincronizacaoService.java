package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.escritorio.entity.AgendaItemGrupo;
import br.com.jogadoresanonimos.escritorio.entity.DiaSemana;
import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import br.com.jogadoresanonimos.escritorio.repository.GrupoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniao;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniaoComparator;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReuniaoGrupoSincronizacaoService {

    private static final String DESCRICAO_ENDERECO = "Reunião presencial cadastrada em Endereços";
    private static final Pattern HORA_INICIAL = Pattern.compile(
            "^\\s*([01]?\\d|2[0-3])\\s*(?:h|:)", Pattern.CASE_INSENSITIVE);

    private final ReuniaoRepository reuniaoRepository;
    private final GrupoIntergrupoRepository grupoRepository;
    private final EstadoIntergrupoRepository estadoRepository;

    public ReuniaoGrupoSincronizacaoService(ReuniaoRepository reuniaoRepository,
                                             GrupoIntergrupoRepository grupoRepository,
                                             EstadoIntergrupoRepository estadoRepository) {
        this.reuniaoRepository = reuniaoRepository;
        this.grupoRepository = grupoRepository;
        this.estadoRepository = estadoRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Reuniao criarReuniaoCanonica(GrupoIntergrupo grupo, DadosEnderecoReuniao endereco) {
        List<AgendaItemReuniao> agenda = paraAgendaReuniao(grupo.getAgenda());
        AgendaItemReuniao primeiro = agenda.getFirst();
        String dia = nomeLegivel(primeiro.getDiaSemana());
        Reuniao reuniao = new Reuniao(grupo.getNome(), CategoriaReuniao.presencial, DESCRICAO_ENDERECO,
                dia, primeiro.getHorario(), grupo.getEndereco(), null, dia, resolverPeriodo(primeiro.getHorario()),
                endereco.latitude(), endereco.longitude(), endereco.cep(), endereco.numero(), endereco.logradouro(),
                endereco.bairro(), endereco.cidade(), endereco.estado(), null);
        reuniao.substituirAgenda(agenda);
        reuniao.vincularGrupoIntergrupo(grupo);
        return reuniaoRepository.save(reuniao);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public GrupoIntergrupo criarGrupoParaReuniaoOrfa(Reuniao reuniao, EstadoIntergrupo estado, int ordem) {
        validarCamposParaEspelho(reuniao);
        GrupoIntergrupo grupo = new GrupoIntergrupo(estado, reuniao.getNome(), reuniao.getLocal(),
                reuniao.getBairro(), reuniao.getCidade(), paraAgendaGrupo(reuniao.getAgenda()), ordem);
        grupo = grupoRepository.save(grupo);
        reuniao.vincularGrupoIntergrupo(grupo);
        return grupo;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void vincularSeSolicitado(Reuniao reuniao, UUID grupoIntergrupoId) {
        if (reuniao.getCategoria() == CategoriaReuniao.virtual) {
            if (grupoIntergrupoId != null) {
                throw new ValidationException(ErrorCode.REUNIAO_GRUPO_DADOS_INVALIDOS,
                        "Reunião virtual não pode possuir grupo intergrupo");
            }
            return;
        }
        if (grupoIntergrupoId == null) {
            throw new ValidationException(ErrorCode.REUNIAO_GRUPO_DADOS_INVALIDOS,
                    "Reunião presencial exige grupo intergrupo vinculado");
        }
        GrupoIntergrupo grupo = bloquearGrupo(grupoIntergrupoId);
        Optional<Reuniao> existente = reuniaoRepository.buscarPorGrupoIntergrupoIdComLock(grupoIntergrupoId);
        if (existente.isPresent() && !existente.get().getId().equals(reuniao.getId())) {
            throw new ConflitoDeEstadoException(ErrorCode.REUNIAO_GRUPO_CONFLITO,
                    "Grupo intergrupo já está vinculado a outra reunião");
        }
        validarCamposParaEspelho(reuniao);
        reuniao.vincularGrupoIntergrupo(grupo);
        validarUfOrganizacional(reuniao, grupo);
        reuniao.atualizarPeriodo(resolverPeriodo(reuniao.getAgenda().getFirst().getHorario()));
        espelharReuniaoNoGrupo(reuniao, grupo);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void sincronizarAposEdicaoReuniao(Reuniao reuniao) {
        if (reuniao.getGrupoIntergrupo() == null) {
            if (reuniao.getCategoria() == CategoriaReuniao.presencial) {
                throw new ValidationException(ErrorCode.REUNIAO_GRUPO_DADOS_INVALIDOS,
                        "Reunião presencial exige grupo intergrupo vinculado");
            }
            return;
        }
        if (reuniao.getCategoria() == CategoriaReuniao.virtual) {
            throw new ConflitoDeEstadoException(ErrorCode.REUNIAO_GRUPO_CONFLITO,
                    "Remova o endereço vinculado antes de converter a reunião para virtual");
        }
        validarCamposParaEspelho(reuniao);
        GrupoIntergrupo grupo = bloquearGrupo(reuniao.getGrupoIntergrupo().getId());
        validarUfOrganizacional(reuniao, grupo);
        List<AgendaItemReuniao> agendaOrdenada = reuniao.getAgenda().stream()
                .sorted(AgendaItemReuniaoComparator.porDiaEHorario()).toList();
        reuniao.atualizarPeriodo(resolverPeriodo(agendaOrdenada.getFirst().getHorario()));
        espelharReuniaoNoGrupo(reuniao, grupo);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<GrupoIntergrupo> sincronizarEdicaoPorGrupo(UUID grupoId, String nome, String enderecoTexto,
                                                                DadosEnderecoReuniao endereco,
                                                                List<AgendaItemGrupo> agenda,
                                                                Instant atualizadoEmEsperado) {
        GrupoIntergrupo grupo = bloquearGrupo(grupoId);
        Optional<Reuniao> vinculada = reuniaoRepository.buscarPorGrupoIntergrupoIdComLock(grupoId);
        if (vinculada.isEmpty()) {
            return Optional.empty();
        }
        Reuniao reuniao = vinculada.get();
        validarVersao(grupo.getAtualizadoEm(), atualizadoEmEsperado);
        List<AgendaItemReuniao> agendaCanonica = paraAgendaReuniao(agenda);
        reuniao.sincronizarCamposCompartilhados(nome, enderecoTexto, endereco.cep(), endereco.numero(),
                endereco.logradouro(), endereco.bairro(), endereco.cidade(), endereco.estado(),
                endereco.latitude(), endereco.longitude(), endereco.estruturaDetalhadaFornecida(),
                agendaCanonica, resolverPeriodo(agendaCanonica.getFirst().getHorario()));
        espelharReuniaoNoGrupo(reuniao, grupo);
        return Optional.of(grupo);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void validarVersao(Instant atual, Instant esperado) {
        if (esperado != null && !atual.truncatedTo(ChronoUnit.MILLIS)
                .equals(esperado.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ConflitoDeEstadoException(ErrorCode.REUNIAO_VERSAO_CONFLITO,
                    "O registro foi alterado por outra operação; recarregue antes de salvar");
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void removerPorReuniao(Reuniao reuniao) {
        GrupoIntergrupo grupo = reuniao.getGrupoIntergrupo();
        reuniaoRepository.delete(reuniao);
        reuniaoRepository.flush();
        if (grupo != null) {
            grupoRepository.delete(grupo);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void removerPorGrupo(UUID grupoId, UUID estadoId) {
        GrupoIntergrupo grupo = bloquearGrupo(grupoId);
        if (!grupo.getEstado().getId().equals(estadoId)) {
            throw new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_GRUPO_NAO_ENCONTRADO,
                    "Grupo não encontrado neste estado");
        }
        Optional<Reuniao> vinculada = reuniaoRepository.buscarPorGrupoIntergrupoIdComLock(grupoId);
        vinculada.ifPresent(reuniao -> {
            reuniaoRepository.delete(reuniao);
            reuniaoRepository.flush();
        });
        grupoRepository.delete(grupo);
    }

    public boolean possuiVinculoNoEstado(UUID estadoId) {
        return reuniaoRepository.existsByGrupoIntergrupoEstadoId(estadoId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Reuniao bloquearReuniaoParaEscrita(UUID reuniaoId) {
        Optional<UUID> grupoId = reuniaoRepository.buscarGrupoIntergrupoIdPorReuniaoId(reuniaoId);
        if (grupoId.isPresent()) {
            GrupoIntergrupo grupo = bloquearGrupo(grupoId.get());
            Reuniao reuniao = reuniaoRepository.buscarPorIdComLock(reuniaoId)
                    .orElseThrow(this::reuniaoNaoEncontrada);
            if (reuniao.getGrupoIntergrupo() == null
                    || !grupo.getId().equals(reuniao.getGrupoIntergrupo().getId())) {
                throw conflitoConcorrente();
            }
            return reuniao;
        }
        Reuniao reuniao = reuniaoRepository.buscarPorIdComLock(reuniaoId)
                .orElseThrow(this::reuniaoNaoEncontrada);
        if (reuniao.getGrupoIntergrupo() != null) {
            throw conflitoConcorrente();
        }
        return reuniao;
    }

    private GrupoIntergrupo bloquearGrupo(UUID grupoId) {
        UUID estadoId = grupoRepository.buscarEstadoIdPorGrupoId(grupoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_GRUPO_NAO_ENCONTRADO,
                        "Grupo intergrupo não encontrado"));
        estadoRepository.buscarPorIdComLock(estadoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_ESTADO_NAO_ENCONTRADO,
                        "Estado do grupo intergrupo não encontrado"));
        GrupoIntergrupo grupo = grupoRepository.buscarPorIdComLock(grupoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_GRUPO_NAO_ENCONTRADO,
                        "Grupo intergrupo não encontrado"));
        if (!estadoId.equals(grupo.getEstado().getId())) {
            throw conflitoConcorrente();
        }
        return grupo;
    }

    private RecursoNaoEncontradoException reuniaoNaoEncontrada() {
        return new RecursoNaoEncontradoException(ErrorCode.REUNIAO_NAO_ENCONTRADA,
                "Reunião não encontrada");
    }

    private ConflitoDeEstadoException conflitoConcorrente() {
        return new ConflitoDeEstadoException(ErrorCode.REUNIAO_VERSAO_CONFLITO,
                "O vínculo foi alterado por outra operação; recarregue antes de continuar");
    }

    private void espelharReuniaoNoGrupo(Reuniao reuniao, GrupoIntergrupo grupo) {
        grupo.editar(reuniao.getNome(), reuniao.getLocal(), reuniao.getBairro(), reuniao.getCidade(),
                paraAgendaGrupo(reuniao.getAgenda()));
    }

    private void validarCamposParaEspelho(Reuniao reuniao) {
        if (reuniao.getLocal() == null || reuniao.getLocal().isBlank()
                || reuniao.getCidade() == null || reuniao.getCidade().isBlank()) {
            throw new ValidationException(ErrorCode.REUNIAO_GRUPO_DADOS_INVALIDOS,
                    "Reunião vinculada exige local e cidade para o espelho de Endereços");
        }
        if (reuniao.getAgenda().isEmpty()) {
            throw new ValidationException(ErrorCode.REUNIAO_GRUPO_DADOS_INVALIDOS,
                    "Reunião vinculada exige agenda");
        }
    }

    private void validarUfOrganizacional(Reuniao reuniao, GrupoIntergrupo grupo) {
        String ufOrganizacional = grupo.getEstado().getUf().name();
        if (reuniao.getEstado() == null || !ufOrganizacional.equalsIgnoreCase(reuniao.getEstado())) {
            throw new ConflitoDeEstadoException(ErrorCode.REUNIAO_GRUPO_UF_DIVERGENTE,
                    "A UF da reunião vinculada deve ser a mesma UF organizacional do grupo");
        }
    }

    private List<AgendaItemReuniao> paraAgendaReuniao(List<AgendaItemGrupo> agenda) {
        return agenda.stream()
                .map(item -> new AgendaItemReuniao(DiaSemanaReuniao.valueOf(item.getDiaSemana().name()),
                        item.getHorario()))
                .sorted(AgendaItemReuniaoComparator.porDiaEHorario())
                .toList();
    }

    private List<AgendaItemGrupo> paraAgendaGrupo(List<AgendaItemReuniao> agenda) {
        return agenda.stream()
                .map(item -> new AgendaItemGrupo(DiaSemana.valueOf(item.getDiaSemana().name()), item.getHorario()))
                .toList();
    }

    private String nomeLegivel(DiaSemanaReuniao diaSemana) {
        return switch (diaSemana) {
            case SEGUNDA -> "Segunda";
            case TERCA -> "Terça";
            case QUARTA -> "Quarta";
            case QUINTA -> "Quinta";
            case SEXTA -> "Sexta";
            case SABADO -> "Sábado";
            case DOMINGO -> "Domingo";
        };
    }

    private String resolverPeriodo(String horario) {
        if (horario == null) {
            return null;
        }
        Matcher matcher = HORA_INICIAL.matcher(horario);
        if (!matcher.find()) {
            return null;
        }
        int hora = Integer.parseInt(matcher.group(1));
        if (hora < 12) {
            return "Manhã";
        }
        return hora < 18 ? "Tarde" : "Noite";
    }
}

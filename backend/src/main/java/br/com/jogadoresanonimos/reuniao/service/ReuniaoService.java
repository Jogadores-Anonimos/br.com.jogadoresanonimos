package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoAgendaItemRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoAvisoResponse;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoResponse;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniao;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniaoComparator;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class ReuniaoService {

    private static final String WHATSAPP_PADRAO = "11 99571-6942";

    private final ReuniaoRepository reuniaoRepository;
    private final ReuniaoGrupoSincronizacaoService sincronizacaoService;

    public ReuniaoService(ReuniaoRepository reuniaoRepository) {
        this(reuniaoRepository, null);
    }

    @Autowired
    public ReuniaoService(ReuniaoRepository reuniaoRepository,
                           ReuniaoGrupoSincronizacaoService sincronizacaoService) {
        this.reuniaoRepository = reuniaoRepository;
        this.sincronizacaoService = sincronizacaoService;
    }

    @Transactional(readOnly = true)
    public List<ReuniaoResponse> listarTodas() {
        return reuniaoRepository.findAllByOrderByNomeAsc().stream()
                .map(ReuniaoResponse::from)
                .toList();
    }

    @Transactional
    public ReuniaoResponse criar(CriarReuniaoRequest request) {
        DadosAgenda dadosAgenda = normalizarAgenda(request.agenda(), request.dia(), request.horario(),
                request.diaSemana(), request.periodo());
        Reuniao reuniao = new Reuniao(request.nome().trim(), request.categoria(),
                validarEDescricao(request.categoria(), request.descricao()),
                dadosAgenda.dia(), dadosAgenda.horario(), normalizarLocal(request.local()),
                normalizarWhatsapp(request.whatsapp()), dadosAgenda.diaSemana(), dadosAgenda.periodo(),
                request.latitude(), request.longitude(), normalizar(request.cep()), normalizar(request.numero()),
                normalizar(request.logradouro()), normalizar(request.bairro()), normalizar(request.cidade()),
                validarEstadoVirtual(request.categoria(), normalizarEstado(request.estado())),
                normalizar(request.linkReuniao()));
        reuniao.substituirAgenda(dadosAgenda.itens());
        reuniao = reuniaoRepository.save(reuniao);
        if (sincronizacaoService != null) {
            sincronizacaoService.vincularSeSolicitado(reuniao, request.grupoIntergrupoId());
        }
        return ReuniaoResponse.from(reuniao);
    }

    @Transactional
    public ReuniaoResponse editar(UUID id, EditarReuniaoRequest request) {
        Reuniao reuniao = buscarParaEscrita(id);
        if (sincronizacaoService != null) {
            sincronizacaoService.validarVersao(reuniao.getAtualizadoEm(), request.atualizadoEm());
        }
        DadosAgenda dadosAgenda = normalizarAgendaEdicao(reuniao, request);
        String localAntes = reuniao.getLocal();
        String cidadeAntes = reuniao.getCidade();
        String estadoAntes = reuniao.getEstado();
        String localDepois = normalizarLocal(request.local());
        String cidadeDepois = normalizar(request.cidade());
        String estadoDepois = validarEstadoVirtual(request.categoria(), normalizarEstado(request.estado()));
        reuniao.editar(request.nome().trim(), request.categoria(),
                validarEDescricao(request.categoria(), request.descricao()),
                dadosAgenda.dia(), dadosAgenda.horario(), localDepois,
                normalizarWhatsapp(request.whatsapp()), dadosAgenda.diaSemana(), dadosAgenda.periodo(),
                request.latitude(), request.longitude(), normalizar(request.cep()), normalizar(request.numero()),
                normalizar(request.logradouro()), normalizar(request.bairro()), cidadeDepois,
                estadoDepois, normalizar(request.linkReuniao()));
        reuniao.substituirAgenda(dadosAgenda.itens());
        if (sincronizacaoService != null) {
            sincronizacaoService.sincronizarAposEdicaoReuniao(reuniao);
        }
        List<ReuniaoAvisoResponse> avisos = detectarAvisos(localAntes, localDepois, cidadeAntes, cidadeDepois,
                estadoAntes, estadoDepois);
        return ReuniaoResponse.from(reuniao, avisos);
    }

    private List<ReuniaoAvisoResponse> detectarAvisos(String localAntes, String localDepois,
                                                        String cidadeAntes, String cidadeDepois,
                                                        String estadoAntes, String estadoDepois) {
        List<ReuniaoAvisoResponse> avisos = new ArrayList<>();
        boolean enderecoEstruturadoMudou = !Objects.equals(cidadeAntes, cidadeDepois)
                || !Objects.equals(estadoAntes, estadoDepois);
        boolean localNaoMudou = Objects.equals(localAntes, localDepois);
        if (enderecoEstruturadoMudou && localNaoMudou) {
            avisos.add(ReuniaoAvisoResponse.enderecoLocalDivergente());
        }
        return avisos;
    }

    @Transactional
    public void remover(UUID id) {
        Reuniao reuniao = buscarParaEscrita(id);
        if (sincronizacaoService == null) {
            reuniaoRepository.delete(reuniao);
        } else {
            sincronizacaoService.removerPorReuniao(reuniao);
        }
    }

    private String validarEDescricao(CategoriaReuniao categoria, String descricao) {
        String normalizada = descricao == null ? "" : descricao.trim();
        if (categoria == CategoriaReuniao.presencial && normalizada.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Descrição é obrigatória para reunião presencial");
        }
        return normalizada;
    }

    private String validarEstadoVirtual(CategoriaReuniao categoria, String estado) {
        if (categoria == CategoriaReuniao.virtual && estado == null) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Estado é obrigatório para reunião virtual");
        }
        return estado;
    }

    private String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    private String normalizarLocal(String valor) {
        return (valor == null || valor.isBlank()) ? "" : valor.trim();
    }

    private String normalizarEstado(String valor) {
        String normalizado = normalizar(valor);
        return normalizado == null ? null : normalizado.toUpperCase();
    }

    private String normalizarWhatsapp(String valor) {
        String normalizado = normalizar(valor);
        return normalizado == null ? WHATSAPP_PADRAO : normalizado;
    }

    private DadosAgenda normalizarAgenda(List<ReuniaoAgendaItemRequest> agenda, String dia, String horario,
                                          String diaSemana, String periodo) {
        boolean agendaNova = agenda != null && !agenda.isEmpty();
        List<AgendaItemReuniao> itens = agendaNova
                ? normalizarItensAgenda(agenda)
                : normalizarItemLegado(dia, horario, diaSemana);
        AgendaItemReuniao primeiro = itens.getFirst();
        String diaRaiz = agendaNova ? nomeLegivel(primeiro.getDiaSemana()) : exigirTexto(dia, "Dia é obrigatório");
        String diaSemanaRaiz = agendaNova ? nomeLegivel(primeiro.getDiaSemana()) : normalizar(diaSemana);
        return new DadosAgenda(itens, diaRaiz, primeiro.getHorario(), diaSemanaRaiz, normalizar(periodo));
    }

    private DadosAgenda normalizarAgendaEdicao(Reuniao reuniao, EditarReuniaoRequest request) {
        if (request.agenda() != null && !request.agenda().isEmpty()) {
            return normalizarAgenda(request.agenda(), request.dia(), request.horario(), request.diaSemana(),
                    request.periodo());
        }
        AgendaItemReuniao itemLegado = normalizarItemLegado(request.dia(), request.horario(),
                request.diaSemana()).getFirst();
        List<AgendaItemReuniao> itens = mesclarEdicaoLegada(reuniao, itemLegado);
        AgendaItemReuniao primeiro = itens.getFirst();
        return new DadosAgenda(itens, nomeLegivel(primeiro.getDiaSemana()), primeiro.getHorario(),
                nomeLegivel(primeiro.getDiaSemana()), normalizar(request.periodo()));
    }

    private List<AgendaItemReuniao> mesclarEdicaoLegada(Reuniao reuniao, AgendaItemReuniao itemLegado) {
        List<AgendaItemReuniao> itens = reuniao.getAgenda().stream()
                .filter(item -> item.getDiaSemana() != null)
                .map(item -> new AgendaItemReuniao(item.getDiaSemana(), item.getHorario()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        if (itens.isEmpty()) {
            itens.add(itemLegado);
            return itens;
        }
        itens.sort(comparadorAgenda());
        if (indiceDoItem(itens, itemLegado) >= 0) {
            return itens;
        }
        int indiceDiaRecebido = indiceDoDia(itens, itemLegado.getDiaSemana());
        if (indiceDiaRecebido >= 0) {
            itens.set(indiceDiaRecebido, itemLegado);
        } else {
            DiaSemanaReuniao diaPrimarioAnterior = converterDiaSemana(
                    normalizar(reuniao.getDiaSemana()) == null ? reuniao.getDia() : reuniao.getDiaSemana());
            int indicePrimarioAnterior = indiceDoItem(itens,
                    new AgendaItemReuniao(diaPrimarioAnterior, reuniao.getHorario()));
            if (indicePrimarioAnterior < 0) {
                indicePrimarioAnterior = indiceDoDia(itens, diaPrimarioAnterior);
            }
            itens.set(indicePrimarioAnterior >= 0 ? indicePrimarioAnterior : 0, itemLegado);
        }
        itens.sort(comparadorAgenda());
        return itens;
    }

    private int indiceDoDia(List<AgendaItemReuniao> itens, DiaSemanaReuniao diaSemana) {
        for (int indice = 0; indice < itens.size(); indice++) {
            if (itens.get(indice).getDiaSemana() == diaSemana) {
                return indice;
            }
        }
        return -1;
    }

    private int indiceDoItem(List<AgendaItemReuniao> itens, AgendaItemReuniao itemProcurado) {
        for (int indice = 0; indice < itens.size(); indice++) {
            AgendaItemReuniao item = itens.get(indice);
            if (item.getDiaSemana() == itemProcurado.getDiaSemana()
                    && item.getHorario().equals(itemProcurado.getHorario())) {
                return indice;
            }
        }
        return -1;
    }

    private List<AgendaItemReuniao> normalizarItensAgenda(List<ReuniaoAgendaItemRequest> agenda) {
        if (agenda.size() > 336) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR,
                    "Agenda deve conter no máximo 336 horários");
        }
        Set<ItemAgendaNormalizado> combinacoes = new HashSet<>();
        List<AgendaItemReuniao> itens = new ArrayList<>();
        for (ReuniaoAgendaItemRequest item : agenda) {
            if (item == null) {
                throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Item da agenda é obrigatório");
            }
            DiaSemanaReuniao diaSemana = converterDiaSemana(item.diaSemana());
            String horario = exigirTexto(item.horario(), "Horário é obrigatório");
            if (horario.length() > 50) {
                throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Horário deve ter no máximo 50 caracteres");
            }
            if (!combinacoes.add(new ItemAgendaNormalizado(diaSemana, horario))) {
                throw new ValidationException(ErrorCode.VALIDATION_ERROR,
                        "Agenda não pode repetir o mesmo horário no mesmo dia");
            }
            itens.add(new AgendaItemReuniao(diaSemana, horario));
        }
        itens.sort(comparadorAgenda());
        return itens;
    }

    private Comparator<AgendaItemReuniao> comparadorAgenda() {
        return AgendaItemReuniaoComparator.porDiaEHorario();
    }

    private List<AgendaItemReuniao> normalizarItemLegado(String dia, String horario, String diaSemana) {
        String diaLegado = exigirTexto(dia, "Dia é obrigatório");
        String horarioLegado = exigirTexto(horario, "Horário é obrigatório");
        DiaSemanaReuniao diaNormalizado = converterDiaSemana(normalizar(diaSemana) == null ? diaLegado : diaSemana);
        return List.of(new AgendaItemReuniao(diaNormalizado, horarioLegado));
    }

    private DiaSemanaReuniao converterDiaSemana(String valor) {
        String texto = exigirTexto(valor, "Dia da semana é obrigatório");
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replace("-FEIRA", "")
                .trim();
        try {
            return DiaSemanaReuniao.valueOf(normalizado);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Dia da semana inválido");
        }
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

    private String exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, mensagem);
        }
        return valor.trim();
    }

    private Reuniao buscarOuFalhar(UUID id) {
        return reuniaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.REUNIAO_NAO_ENCONTRADA, "Reunião não encontrada"));
    }

    private Reuniao buscarParaEscrita(UUID id) {
        if (sincronizacaoService == null) {
            return buscarOuFalhar(id);
        }
        return sincronizacaoService.bloquearReuniaoParaEscrita(id);
    }

    private record DadosAgenda(List<AgendaItemReuniao> itens, String dia, String horario,
                               String diaSemana, String periodo) {
    }

    private record ItemAgendaNormalizado(DiaSemanaReuniao diaSemana, String horario) {
    }
}

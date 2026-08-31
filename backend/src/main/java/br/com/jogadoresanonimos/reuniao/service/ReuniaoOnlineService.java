package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.ErrorCode;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ReuniaoOnlineService {

    private final ReuniaoOnlineRepository reuniaoOnlineRepository;

    public ReuniaoOnlineService(ReuniaoOnlineRepository reuniaoOnlineRepository) {
        this.reuniaoOnlineRepository = reuniaoOnlineRepository;
    }

    @Transactional(readOnly = true)
    public List<ReuniaoOnlineResponse> listarTodas() {
        return reuniaoOnlineRepository.findAllByOrderByNomeAsc().stream()
                .map(ReuniaoOnlineResponse::from)
                .toList();
    }

    @Transactional
    public ReuniaoOnlineResponse criar(CriarReuniaoOnlineRequest request) {
        List<AgendaItemReuniaoOnline> agenda = normalizarAgenda(request.agenda());
        ReuniaoOnline reuniaoOnline = new ReuniaoOnline(request.nome().trim(), normalizar(request.telefone()), agenda);
        reuniaoOnline = reuniaoOnlineRepository.save(reuniaoOnline);
        return ReuniaoOnlineResponse.from(reuniaoOnline);
    }

    @Transactional
    public ReuniaoOnlineResponse editar(UUID id, EditarReuniaoOnlineRequest request) {
        ReuniaoOnline reuniaoOnline = buscarOuFalhar(id);
        List<AgendaItemReuniaoOnline> agenda = normalizarAgenda(request.agenda());
        reuniaoOnline.editar(request.nome().trim(), normalizar(request.telefone()), agenda);
        return ReuniaoOnlineResponse.from(reuniaoOnline);
    }

    @Transactional
    public void remover(UUID id) {
        ReuniaoOnline reuniaoOnline = buscarOuFalhar(id);
        reuniaoOnlineRepository.delete(reuniaoOnline);
    }

    private String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    private List<AgendaItemReuniaoOnline> normalizarAgenda(List<ReuniaoOnlineAgendaItemRequest> agenda) {
        Set<ItemAgendaNormalizado> combinacoes = new HashSet<>();
        List<AgendaItemReuniaoOnline> itens = new ArrayList<>();
        for (ReuniaoOnlineAgendaItemRequest item : agenda) {
            DiaSemanaReuniao diaSemana = converterDiaSemana(item.diaSemana());
            String horario = exigirTexto(item.horario(), "Horário é obrigatório");
            if (!combinacoes.add(new ItemAgendaNormalizado(diaSemana, horario))) {
                throw new ValidationException(ErrorCode.VALIDATION_ERROR,
                        "Agenda não pode repetir o mesmo horário no mesmo dia");
            }
            itens.add(new AgendaItemReuniaoOnline(diaSemana, horario));
        }
        return itens;
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

    private String exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, mensagem);
        }
        return valor.trim();
    }

    private ReuniaoOnline buscarOuFalhar(UUID id) {
        return reuniaoOnlineRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.REUNIAO_ONLINE_NAO_ENCONTRADA,
                        "Reunião online não encontrada"));
    }

    private record ItemAgendaNormalizado(DiaSemanaReuniao diaSemana, String horario) {
    }
}

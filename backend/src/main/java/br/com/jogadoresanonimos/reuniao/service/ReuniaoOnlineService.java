package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.imagem.entity.Imagem;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineAgendaItemRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineResponse;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniaoOnline;
import br.com.jogadoresanonimos.reuniao.entity.CorReuniaoOnline;
import br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.ReuniaoOnline;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoOnlineRepository;
import org.springframework.beans.factory.annotation.Value;
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
    private final ImagemRepository imagemRepository;
    private final StorageClient storageClient;
    private final String telefoneInstitucional;

    public ReuniaoOnlineService(ReuniaoOnlineRepository reuniaoOnlineRepository, ImagemRepository imagemRepository,
                                 StorageClient storageClient,
                                 @Value("${reuniao.telefone-institucional-online:21 99472-1933}") String telefoneInstitucional) {
        this.reuniaoOnlineRepository = reuniaoOnlineRepository;
        this.imagemRepository = imagemRepository;
        this.storageClient = storageClient;
        this.telefoneInstitucional = telefoneInstitucional;
    }

    @Transactional(readOnly = true)
    public List<ReuniaoOnlineResponse> listarTodas() {
        return reuniaoOnlineRepository.findAllByOrderByNomeAsc().stream()
                .map(this::paraResponse)
                .toList();
    }

    @Transactional
    public ReuniaoOnlineResponse criar(CriarReuniaoOnlineRequest request) {
        List<AgendaItemReuniaoOnline> agenda = normalizarAgenda(request.agenda());
        String videoUrl = normalizar(request.videoUrl());
        validarMidia(videoUrl, request.panfletoImagemId());
        CorReuniaoOnline cor = request.cor() != null ? request.cor() : CorReuniaoOnline.ROXO;
        ReuniaoOnline reuniaoOnline = new ReuniaoOnline(request.nome().trim(), telefoneInstitucional,
                normalizar(request.descricao()), videoUrl, request.panfletoImagemId(), cor, agenda);
        reuniaoOnline = reuniaoOnlineRepository.save(reuniaoOnline);
        return paraResponse(reuniaoOnline);
    }

    @Transactional
    public ReuniaoOnlineResponse editar(UUID id, EditarReuniaoOnlineRequest request) {
        ReuniaoOnline reuniaoOnline = buscarOuFalhar(id);
        List<AgendaItemReuniaoOnline> agenda = normalizarAgenda(request.agenda());
        String videoUrl = normalizar(request.videoUrl());
        validarMidia(videoUrl, request.panfletoImagemId());
        CorReuniaoOnline cor = request.cor() != null ? request.cor() : reuniaoOnline.getCor();
        reuniaoOnline.editar(request.nome().trim(), telefoneInstitucional, normalizar(request.descricao()),
                videoUrl, request.panfletoImagemId(), cor, agenda);
        return paraResponse(reuniaoOnline);
    }

    private void validarMidia(String videoUrl, UUID panfletoImagemId) {
        if (videoUrl != null && !videoUrl.contains("youtube.com") && !videoUrl.contains("youtu.be")) {
            throw new ValidationException(ErrorCode.REUNIAO_ONLINE_MIDIA_INVALIDA,
                    "videoUrl deve ser um link do YouTube");
        }
        if (panfletoImagemId != null && !imagemRepository.existsById(panfletoImagemId)) {
            throw new ValidationException(ErrorCode.REUNIAO_ONLINE_MIDIA_INVALIDA,
                    "panfletoImagemId não corresponde a um upload existente");
        }
    }

    private ReuniaoOnlineResponse paraResponse(ReuniaoOnline reuniaoOnline) {
        return ReuniaoOnlineResponse.from(reuniaoOnline, resolverPanfletoUrl(reuniaoOnline.getPanfletoImagemId()));
    }

    private String resolverPanfletoUrl(UUID panfletoImagemId) {
        if (panfletoImagemId == null) {
            return null;
        }
        return imagemRepository.findById(panfletoImagemId)
                .map(Imagem::getChaveArquivo)
                .map(storageClient::urlPublicaPara)
                .orElse(null);
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

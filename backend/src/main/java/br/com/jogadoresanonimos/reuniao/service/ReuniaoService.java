package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoResponse;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReuniaoService {

    private static final String WHATSAPP_PADRAO = "11 99571-6942";

    private final ReuniaoRepository reuniaoRepository;

    public ReuniaoService(ReuniaoRepository reuniaoRepository) {
        this.reuniaoRepository = reuniaoRepository;
    }

    @Transactional(readOnly = true)
    public List<ReuniaoResponse> listarTodas() {
        return reuniaoRepository.findAllByOrderByNomeAsc().stream()
                .map(ReuniaoResponse::from)
                .toList();
    }

    @Transactional
    public ReuniaoResponse criar(CriarReuniaoRequest request) {
        Reuniao reuniao = new Reuniao(request.nome().trim(), request.categoria(), request.descricao().trim(),
                request.dia().trim(), request.horario().trim(), normalizar(request.local()),
                normalizarWhatsapp(request.whatsapp()), normalizar(request.diaSemana()), normalizar(request.periodo()),
                request.latitude(), request.longitude(), normalizar(request.cep()), normalizar(request.numero()),
                normalizar(request.logradouro()), normalizar(request.bairro()), normalizar(request.cidade()),
                normalizarEstado(request.estado()), normalizar(request.linkReuniao()));
        reuniao = reuniaoRepository.save(reuniao);
        return ReuniaoResponse.from(reuniao);
    }

    @Transactional
    public ReuniaoResponse editar(UUID id, EditarReuniaoRequest request) {
        Reuniao reuniao = buscarOuFalhar(id);
        reuniao.editar(request.nome().trim(), request.categoria(), request.descricao().trim(),
                request.dia().trim(), request.horario().trim(), normalizar(request.local()),
                normalizarWhatsapp(request.whatsapp()), normalizar(request.diaSemana()), normalizar(request.periodo()),
                request.latitude(), request.longitude(), normalizar(request.cep()), normalizar(request.numero()),
                normalizar(request.logradouro()), normalizar(request.bairro()), normalizar(request.cidade()),
                normalizarEstado(request.estado()), normalizar(request.linkReuniao()));
        return ReuniaoResponse.from(reuniao);
    }

    @Transactional
    public void remover(UUID id) {
        Reuniao reuniao = buscarOuFalhar(id);
        reuniaoRepository.delete(reuniao);
    }

    private String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    private String normalizarEstado(String valor) {
        String normalizado = normalizar(valor);
        return normalizado == null ? null : normalizado.toUpperCase();
    }

    private String normalizarWhatsapp(String valor) {
        String normalizado = normalizar(valor);
        return normalizado == null ? WHATSAPP_PADRAO : normalizado;
    }

    private Reuniao buscarOuFalhar(UUID id) {
        return reuniaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.REUNIAO_NAO_ENCONTRADA, "Reunião não encontrada"));
    }
}

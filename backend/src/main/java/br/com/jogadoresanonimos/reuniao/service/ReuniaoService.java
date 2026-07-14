package br.com.jogadoresanonimos.reuniao.service;

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
                request.dia().trim(), request.horario().trim(), request.local().trim(),
                normalizar(request.whatsapp()), normalizar(request.diaSemana()), normalizar(request.periodo()),
                request.latitude(), request.longitude());
        reuniao = reuniaoRepository.save(reuniao);
        return ReuniaoResponse.from(reuniao);
    }

    @Transactional
    public ReuniaoResponse editar(UUID id, EditarReuniaoRequest request) {
        Reuniao reuniao = buscarOuFalhar(id);
        reuniao.editar(request.nome().trim(), request.categoria(), request.descricao().trim(),
                request.dia().trim(), request.horario().trim(), request.local().trim(),
                normalizar(request.whatsapp()), normalizar(request.diaSemana()), normalizar(request.periodo()),
                request.latitude(), request.longitude());
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

    private Reuniao buscarOuFalhar(UUID id) {
        return reuniaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reunião não encontrada"));
    }
}

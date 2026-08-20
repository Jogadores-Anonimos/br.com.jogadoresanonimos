package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoAvisoResponse;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoResponse;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        Reuniao reuniao = new Reuniao(request.nome().trim(), request.categoria(),
                validarEDescricao(request.categoria(), request.descricao()),
                request.dia().trim(), request.horario().trim(), normalizarLocal(request.local()),
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
        String localAntes = reuniao.getLocal();
        String cidadeAntes = reuniao.getCidade();
        String estadoAntes = reuniao.getEstado();
        String localDepois = normalizarLocal(request.local());
        String cidadeDepois = normalizar(request.cidade());
        String estadoDepois = normalizarEstado(request.estado());
        reuniao.editar(request.nome().trim(), request.categoria(),
                validarEDescricao(request.categoria(), request.descricao()),
                request.dia().trim(), request.horario().trim(), localDepois,
                normalizarWhatsapp(request.whatsapp()), normalizar(request.diaSemana()), normalizar(request.periodo()),
                request.latitude(), request.longitude(), normalizar(request.cep()), normalizar(request.numero()),
                normalizar(request.logradouro()), normalizar(request.bairro()), cidadeDepois,
                estadoDepois, normalizar(request.linkReuniao()));
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
        Reuniao reuniao = buscarOuFalhar(id);
        reuniaoRepository.delete(reuniao);
    }

    private String validarEDescricao(CategoriaReuniao categoria, String descricao) {
        String normalizada = descricao == null ? "" : descricao.trim();
        if (categoria == CategoriaReuniao.presencial && normalizada.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Descrição é obrigatória para reunião presencial");
        }
        return normalizada;
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

    private Reuniao buscarOuFalhar(UUID id) {
        return reuniaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.REUNIAO_NAO_ENCONTRADA, "Reunião não encontrada"));
    }
}

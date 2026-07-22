package br.com.jogadoresanonimos.aviso.service;

import br.com.jogadoresanonimos.aviso.dto.AvisoAdminResponse;
import br.com.jogadoresanonimos.aviso.dto.AvisoPublicoResponse;
import br.com.jogadoresanonimos.aviso.dto.CriarAvisoRequest;
import br.com.jogadoresanonimos.aviso.dto.EditarAvisoRequest;
import br.com.jogadoresanonimos.aviso.entity.Aviso;
import br.com.jogadoresanonimos.aviso.repository.AvisoRepository;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;

    public AvisoService(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    @Transactional(readOnly = true)
    public List<AvisoPublicoResponse> listarAtivos() {
        return avisoRepository.findAtivosOrdenados(Instant.now()).stream()
                .map(AvisoPublicoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvisoAdminResponse> listarTodos() {
        return avisoRepository.findAllByOrderByFixadoDescCriadoEmDesc().stream()
                .map(AvisoAdminResponse::from)
                .toList();
    }

    @Transactional
    public AvisoAdminResponse criar(CriarAvisoRequest request) {
        Aviso aviso = new Aviso(request.titulo().trim(), request.corpo().trim(), request.fixado(),
                request.dataExpiracao());
        aviso = avisoRepository.save(aviso);
        return AvisoAdminResponse.from(aviso);
    }

    @Transactional
    public AvisoAdminResponse editar(UUID id, EditarAvisoRequest request) {
        Aviso aviso = buscarOuFalhar(id);
        aviso.editar(request.titulo().trim(), request.corpo().trim(), request.fixado(), request.dataExpiracao());
        return AvisoAdminResponse.from(aviso);
    }

    @Transactional
    public void remover(UUID id) {
        Aviso aviso = buscarOuFalhar(id);
        avisoRepository.delete(aviso);
    }

    private Aviso buscarOuFalhar(UUID id) {
        return avisoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.AVISO_NAO_ENCONTRADO, "Aviso não encontrado"));
    }
}

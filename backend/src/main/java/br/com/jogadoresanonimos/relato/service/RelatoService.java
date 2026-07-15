package br.com.jogadoresanonimos.relato.service;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.service.AdminAuditoriaService;
import br.com.jogadoresanonimos.common.dto.PageResponse;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.relato.dto.*;
import br.com.jogadoresanonimos.relato.entity.Relato;
import br.com.jogadoresanonimos.relato.entity.StatusRelato;
import br.com.jogadoresanonimos.relato.repository.RelatoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
public class RelatoService {

    public static final int TAMANHO_PAGINA_MAXIMO = 50;
    private static final String ENTIDADE_RELATO = "relato";

    private final RelatoRepository relatoRepository;
    private final RateLimiterService rateLimiterService;
    private final AdminAuditoriaService adminAuditoriaService;

    public RelatoService(RelatoRepository relatoRepository, RateLimiterService rateLimiterService,
                          AdminAuditoriaService adminAuditoriaService) {
        this.relatoRepository = relatoRepository;
        this.rateLimiterService = rateLimiterService;
        this.adminAuditoriaService = adminAuditoriaService;
    }

    @Transactional
    public RelatoEnvioResponse enviar(EnviarRelatoRequest request, UUID autorId) {
        rateLimiterService.check("enviar-relato:" + autorId, 5, Duration.ofMinutes(10));
        String nomeExibicao = normalizarNomeExibicao(request.nomeExibicao());
        Relato relato = new Relato(request.texto().trim(), nomeExibicao, autorId);
        relato = relatoRepository.save(relato);
        return RelatoEnvioResponse.from(relato);
    }

    @Transactional(readOnly = true)
    public PageResponse<RelatoPublicoResponse> listarAprovados(int pagina, int tamanho) {
        Pageable pageable = paginavel(pagina, tamanho, Sort.by(Sort.Direction.DESC, "criadoEm"));
        Page<Relato> page = relatoRepository.findByStatus(StatusRelato.approved, pageable);
        return PageResponse.from(page, RelatoPublicoResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<RelatoAdminResponse> listarPorStatus(StatusRelato status, int pagina, int tamanho) {
        Sort sort = status == StatusRelato.pending
                ? Sort.by(Sort.Direction.ASC, "criadoEm")
                : Sort.by(Sort.Direction.DESC, "atualizadoEm");
        Pageable pageable = paginavel(pagina, tamanho, sort);
        Page<Relato> page = relatoRepository.findByStatus(status, pageable);
        return PageResponse.from(page, RelatoAdminResponse::from);
    }

    @Transactional
    public RelatoAdminResponse editar(UUID id, EditarRelatoRequest request, UUID adminId) {
        Relato relato = buscarOuFalhar(id);
        String statusAnterior = relato.getStatus().name();
        relato.editarTexto(request.texto().trim());
        adminAuditoriaService.registrar(adminId, ENTIDADE_RELATO, id, AcaoAuditoria.EDITAR,
                statusAnterior, relato.getStatus().name());
        return RelatoAdminResponse.from(relato);
    }

    @Transactional
    public RelatoAdminResponse aprovar(UUID id, UUID adminId) {
        Relato relato = buscarOuFalhar(id);
        String statusAnterior = relato.getStatus().name();
        relato.aprovar();
        adminAuditoriaService.registrar(adminId, ENTIDADE_RELATO, id, AcaoAuditoria.APROVAR,
                statusAnterior, relato.getStatus().name());
        return RelatoAdminResponse.from(relato);
    }

    @Transactional
    public RelatoAdminResponse rejeitar(UUID id, UUID adminId) {
        Relato relato = buscarOuFalhar(id);
        String statusAnterior = relato.getStatus().name();
        relato.rejeitar();
        adminAuditoriaService.registrar(adminId, ENTIDADE_RELATO, id, AcaoAuditoria.REJEITAR,
                statusAnterior, relato.getStatus().name());
        return RelatoAdminResponse.from(relato);
    }

    @Transactional
    public RelatoAdminResponse reverter(UUID id, UUID adminId) {
        Relato relato = buscarOuFalhar(id);
        String statusAnterior = relato.getStatus().name();
        relato.reverterParaPendente();
        adminAuditoriaService.registrar(adminId, ENTIDADE_RELATO, id, AcaoAuditoria.REVERTER,
                statusAnterior, relato.getStatus().name());
        return RelatoAdminResponse.from(relato);
    }

    @Transactional
    public void remover(UUID id, UUID adminId) {
        Relato relato = buscarOuFalhar(id);
        String statusAnterior = relato.getStatus().name();
        relatoRepository.delete(relato);
        adminAuditoriaService.registrar(adminId, ENTIDADE_RELATO, id, AcaoAuditoria.REMOVER,
                statusAnterior, statusAnterior);
    }

    private Relato buscarOuFalhar(UUID id) {
        return relatoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relato não encontrado"));
    }

    private String normalizarNomeExibicao(String nomeExibicao) {
        if (nomeExibicao == null || nomeExibicao.isBlank()) {
            return null;
        }
        return nomeExibicao.trim();
    }

    private Pageable paginavel(int pagina, int tamanho, Sort sort) {
        int tamanhoSeguro = Math.min(Math.max(tamanho, 1), TAMANHO_PAGINA_MAXIMO);
        int paginaSegura = Math.max(pagina, 0);
        return PageRequest.of(paginaSegura, tamanhoSeguro, sort);
    }
}

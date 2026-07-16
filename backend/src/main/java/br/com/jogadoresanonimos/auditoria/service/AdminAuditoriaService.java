package br.com.jogadoresanonimos.auditoria.service;

import br.com.jogadoresanonimos.auditoria.dto.AuditoriaLogResponse;
import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.entity.AdminAuditoriaLog;
import br.com.jogadoresanonimos.auditoria.repository.AdminAuditoriaLogRepository;
import br.com.jogadoresanonimos.auditoria.repository.AdminAuditoriaLogSpecifications;
import br.com.jogadoresanonimos.common.dto.PageResponse;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AdminAuditoriaService {

    public static final int TAMANHO_PAGINA_MAXIMO = 50;

    private final AdminAuditoriaLogRepository adminAuditoriaLogRepository;

    public AdminAuditoriaService(AdminAuditoriaLogRepository adminAuditoriaLogRepository) {
        this.adminAuditoriaLogRepository = adminAuditoriaLogRepository;
    }

    @Transactional
    public void registrar(UUID adminId, String entidade, UUID entidadeId, AcaoAuditoria acao,
                           String statusAnterior, String statusNovo) {
        adminAuditoriaLogRepository.save(
                new AdminAuditoriaLog(adminId, entidade, entidadeId, acao, statusAnterior, statusNovo));
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditoriaLogResponse> listar(String entidade, AcaoAuditoria acao, Instant dataInicio,
                                                       Instant dataFim, int pagina, int tamanho) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new ValidationException("dataInicio não pode ser posterior a dataFim.");
        }
        Pageable pageable = paginavel(pagina, tamanho);
        Page<AdminAuditoriaLog> page = adminAuditoriaLogRepository.findAll(
                AdminAuditoriaLogSpecifications.comFiltros(entidade, acao, dataInicio, dataFim), pageable);
        return PageResponse.from(page, AuditoriaLogResponse::from);
    }

    private Pageable paginavel(int pagina, int tamanho) {
        int tamanhoSeguro = Math.min(Math.max(tamanho, 1), TAMANHO_PAGINA_MAXIMO);
        int paginaSegura = Math.max(pagina, 0);
        return PageRequest.of(paginaSegura, tamanhoSeguro, Sort.by(Sort.Direction.DESC, "criadoEm"));
    }
}

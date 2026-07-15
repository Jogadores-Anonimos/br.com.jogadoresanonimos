package br.com.jogadoresanonimos.auditoria.service;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.entity.AdminAuditoriaLog;
import br.com.jogadoresanonimos.auditoria.repository.AdminAuditoriaLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdminAuditoriaService {

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
}

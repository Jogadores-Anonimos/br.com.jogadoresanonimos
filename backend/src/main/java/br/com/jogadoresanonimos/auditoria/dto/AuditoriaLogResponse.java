package br.com.jogadoresanonimos.auditoria.dto;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.entity.AdminAuditoriaLog;

import java.time.Instant;
import java.util.UUID;

public record AuditoriaLogResponse(UUID id, UUID adminId, String entidade, UUID entidadeId, AcaoAuditoria acao,
                                    String statusAnterior, String statusNovo, Instant criadoEm) {

    public static AuditoriaLogResponse from(AdminAuditoriaLog log) {
        return new AuditoriaLogResponse(log.getId(), log.getAdminId(), log.getEntidade(), log.getEntidadeId(),
                log.getAcao(), log.getStatusAnterior(), log.getStatusNovo(), log.getCriadoEm());
    }
}

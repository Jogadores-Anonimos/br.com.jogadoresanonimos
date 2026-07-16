package br.com.jogadoresanonimos.auditoria.repository;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.entity.AdminAuditoriaLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class AdminAuditoriaLogSpecifications {

    private AdminAuditoriaLogSpecifications() {
    }

    public static Specification<AdminAuditoriaLog> comFiltros(String entidade, AcaoAuditoria acao,
                                                                Instant dataInicio, Instant dataFim) {
        Specification<AdminAuditoriaLog> spec = (root, query, cb) -> cb.conjunction();
        if (entidade != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("entidade"), entidade));
        }
        if (acao != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("acao"), acao));
        }
        if (dataInicio != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("criadoEm"), dataInicio));
        }
        if (dataFim != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("criadoEm"), dataFim));
        }
        return spec;
    }
}

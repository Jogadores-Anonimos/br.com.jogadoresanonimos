package br.com.jogadoresanonimos.auditoria.repository;

import br.com.jogadoresanonimos.auditoria.entity.AdminAuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.UUID;

public interface AdminAuditoriaLogRepository
        extends JpaRepository<AdminAuditoriaLog, UUID>, JpaSpecificationExecutor<AdminAuditoriaLog> {

    long deleteByCriadoEmBefore(Instant limite);
}

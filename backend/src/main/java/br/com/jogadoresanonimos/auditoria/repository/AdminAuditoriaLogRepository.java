package br.com.jogadoresanonimos.auditoria.repository;

import br.com.jogadoresanonimos.auditoria.entity.AdminAuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminAuditoriaLogRepository extends JpaRepository<AdminAuditoriaLog, UUID> {
}

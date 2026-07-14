package br.com.jogadoresanonimos.cms.repository;

import br.com.jogadoresanonimos.cms.entity.CmsAlteracaoLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CmsAlteracaoLogRepository extends JpaRepository<CmsAlteracaoLog, UUID> {
}

package br.com.jogadoresanonimos.cms.repository;

import br.com.jogadoresanonimos.cms.entity.CmsBloco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CmsBlocoRepository extends JpaRepository<CmsBloco, UUID> {

    boolean existsByImagemId(UUID imagemId);

    boolean existsByArquivoId(UUID arquivoId);
}

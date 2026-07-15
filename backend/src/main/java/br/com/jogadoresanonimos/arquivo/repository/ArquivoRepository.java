package br.com.jogadoresanonimos.arquivo.repository;

import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ArquivoRepository extends JpaRepository<Arquivo, UUID> {
    List<Arquivo> findAllByOrderByCriadoEmDesc();

    @Query("""
            SELECT a.id FROM Arquivo a
            WHERE a.criadoEm < :limite
            AND NOT EXISTS (SELECT 1 FROM CmsBloco b WHERE b.arquivoId = a.id)
            AND NOT EXISTS (SELECT 1 FROM UsuarioArquivo ua WHERE ua.id.arquivoId = a.id)
            """)
    List<UUID> buscarIdsOrfaosAnterioresA(@Param("limite") Instant limite);
}

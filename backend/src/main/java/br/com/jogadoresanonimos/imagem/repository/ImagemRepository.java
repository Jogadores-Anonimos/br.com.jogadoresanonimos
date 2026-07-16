package br.com.jogadoresanonimos.imagem.repository;

import br.com.jogadoresanonimos.imagem.entity.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ImagemRepository extends JpaRepository<Imagem, UUID> {

    @Query("""
            SELECT i.id FROM Imagem i
            WHERE i.criadoEm < :limite
            AND NOT EXISTS (SELECT 1 FROM CmsBloco b WHERE b.imagemId = i.id)
            AND NOT EXISTS (SELECT 1 FROM BannerMural m WHERE m.imagemId = i.id)
            """)
    List<UUID> buscarIdsOrfaasAnterioresA(@Param("limite") Instant limite);

    @Query("SELECT i.id, i.chaveArquivo FROM Imagem i WHERE i.id IN :ids")
    List<Object[]> findIdEChavePorIdIn(@Param("ids") List<UUID> ids);
}

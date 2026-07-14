package br.com.jogadoresanonimos.aviso.repository;

import br.com.jogadoresanonimos.aviso.entity.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AvisoRepository extends JpaRepository<Aviso, UUID> {

    @Query("SELECT a FROM Aviso a WHERE a.dataExpiracao IS NULL OR a.dataExpiracao > :agora " +
            "ORDER BY a.fixado DESC, a.criadoEm DESC")
    List<Aviso> findAtivosOrdenados(@Param("agora") Instant agora);

    List<Aviso> findAllByOrderByFixadoDescCriadoEmDesc();
}

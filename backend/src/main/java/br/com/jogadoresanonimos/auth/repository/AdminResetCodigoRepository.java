package br.com.jogadoresanonimos.auth.repository;

import br.com.jogadoresanonimos.auth.entity.AdminResetCodigo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AdminResetCodigoRepository extends JpaRepository<AdminResetCodigo, UUID> {

    boolean existsByCodigoHash(String codigoHash);

    @Query("SELECT a FROM AdminResetCodigo a WHERE a.codigoHash = :codigoHash AND a.usadoEm IS NULL AND a.expiraEm > :agora")
    Optional<AdminResetCodigo> findValidoPorCodigoHash(@Param("codigoHash") String codigoHash, @Param("agora") Instant agora);
}

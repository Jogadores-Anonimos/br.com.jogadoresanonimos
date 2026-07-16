package br.com.jogadoresanonimos.security.repository;

import br.com.jogadoresanonimos.security.entity.JwtDenylistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface JwtDenylistRepository extends JpaRepository<JwtDenylistEntry, UUID> {

    boolean existsByJti(String jti);

    long deleteByExpiraEmBefore(Instant limite);
}

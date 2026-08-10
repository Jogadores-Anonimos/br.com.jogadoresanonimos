package br.com.jogadoresanonimos.auth.repository;

import br.com.jogadoresanonimos.auth.entity.EmailConfirmacaoToken;
import br.com.jogadoresanonimos.auth.entity.TipoEmailConfirmacaoToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailConfirmacaoTokenRepository extends JpaRepository<EmailConfirmacaoToken, UUID> {

    Optional<EmailConfirmacaoToken> findByTokenHash(String tokenHash);

    List<EmailConfirmacaoToken> findByUsuarioIdAndTipoAndUsadoEmIsNull(UUID usuarioId, TipoEmailConfirmacaoToken tipo);
}

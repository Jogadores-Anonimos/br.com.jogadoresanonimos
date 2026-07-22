package br.com.jogadoresanonimos.usuario.repository;

import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByApelidoIgnoreCaseOrEmailIgnoreCase(String apelido, String email);

    boolean existsByApelidoIgnoreCase(String apelido);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRole(Role role);

    long countByRole(Role role);

    List<Usuario> findAllByOrderByCriadoEmDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Usuario u WHERE u.role = :role ORDER BY u.id ASC")
    List<Usuario> buscarPorRoleParaAtualizacao(Role role);
}

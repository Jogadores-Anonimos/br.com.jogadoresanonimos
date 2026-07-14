package br.com.jogadoresanonimos.usuario.repository;

import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

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
}

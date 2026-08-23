package br.com.jogadoresanonimos.reuniao.repository;

import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReuniaoRepository extends JpaRepository<Reuniao, UUID> {

    List<Reuniao> findAllByOrderByNomeAsc();

    List<Reuniao> findAllByCategoriaAndEstadoIsNullAndLatitudeIsNotNullAndLongitudeIsNotNull(CategoriaReuniao categoria);

    Optional<Reuniao> findByGrupoIntergrupoId(UUID grupoIntergrupoId);

    boolean existsByGrupoIntergrupoEstadoId(UUID estadoId);

    boolean existsByGrupoIntergrupoEstadoRegiaoId(UUID regiaoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reuniao r WHERE r.id = :id")
    Optional<Reuniao> buscarPorIdComLock(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reuniao r WHERE r.grupoIntergrupo.id = :grupoIntergrupoId")
    Optional<Reuniao> buscarPorGrupoIntergrupoIdComLock(UUID grupoIntergrupoId);

    @Query("SELECT r.grupoIntergrupo.id FROM Reuniao r WHERE r.id = :id")
    Optional<UUID> buscarGrupoIntergrupoIdPorReuniaoId(UUID id);
}

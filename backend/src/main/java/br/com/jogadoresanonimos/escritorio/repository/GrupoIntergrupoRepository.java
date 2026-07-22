package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrupoIntergrupoRepository extends JpaRepository<GrupoIntergrupo, UUID> {

    List<GrupoIntergrupo> findAllByEstadoIdOrderByOrdemAsc(UUID estadoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM GrupoIntergrupo g WHERE g.id = :id")
    Optional<GrupoIntergrupo> buscarPorIdComLock(UUID id);
}

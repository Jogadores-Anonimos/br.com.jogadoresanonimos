package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstadoIntergrupoRepository extends JpaRepository<EstadoIntergrupo, UUID> {

    List<EstadoIntergrupo> findAllByRegiaoIdOrderByOrdemAsc(UUID regiaoId);

    boolean existsByRegiaoIdAndUf(UUID regiaoId, UnidadeFederativa uf);

    Optional<EstadoIntergrupo> findByRegiaoIdAndUf(UUID regiaoId, UnidadeFederativa uf);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EstadoIntergrupo e WHERE e.id = :id")
    Optional<EstadoIntergrupo> buscarPorIdComLock(UUID id);
}

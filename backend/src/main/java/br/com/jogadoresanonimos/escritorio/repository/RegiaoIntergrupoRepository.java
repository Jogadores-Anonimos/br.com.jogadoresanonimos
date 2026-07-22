package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegiaoIntergrupoRepository extends JpaRepository<RegiaoIntergrupo, UUID> {

    List<RegiaoIntergrupo> findAllByOrderByOrdemAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RegiaoIntergrupo r WHERE r.id = :id")
    Optional<RegiaoIntergrupo> buscarPorIdComLock(UUID id);

    @Query("SELECT DISTINCT r FROM RegiaoIntergrupo r " +
            "LEFT JOIN FETCH r.estados " +
            "ORDER BY r.ordem ASC")
    List<RegiaoIntergrupo> findAllComEstados();

    @Query("SELECT DISTINCT e FROM EstadoIntergrupo e " +
            "LEFT JOIN FETCH e.grupos " +
            "WHERE e.regiao IN :regioes")
    List<EstadoIntergrupo> carregarGruposDosEstados(List<RegiaoIntergrupo> regioes);

    @Query("SELECT DISTINCT g FROM GrupoIntergrupo g " +
            "LEFT JOIN FETCH g.telefones " +
            "WHERE g.estado IN :estados")
    List<GrupoIntergrupo> carregarTelefonesDosGrupos(List<EstadoIntergrupo> estados);

    boolean existsByNomeIgnoreCase(String nome);
}

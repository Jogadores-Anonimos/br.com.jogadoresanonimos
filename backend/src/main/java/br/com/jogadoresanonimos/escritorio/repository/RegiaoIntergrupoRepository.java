package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RegiaoIntergrupoRepository extends JpaRepository<RegiaoIntergrupo, UUID> {

    List<RegiaoIntergrupo> findAllByOrderByOrdemAsc();

    // Só um bag (r.estados) é buscado com JOIN FETCH — Hibernate não
    // permite fetch eager de duas coleções List (bag) na mesma query
    // (MultipleBagFetchException). e.grupos carrega lazy, resolvido
    // dentro da mesma transação read-only do service, sem N+1 explosivo
    // dado o volume baixo esperado (poucas regiões/estados).
    @Query("SELECT DISTINCT r FROM RegiaoIntergrupo r " +
            "LEFT JOIN FETCH r.estados e " +
            "ORDER BY r.ordem ASC")
    List<RegiaoIntergrupo> findAllComHierarquiaCompleta();

    boolean existsByNomeIgnoreCase(String nome);
}

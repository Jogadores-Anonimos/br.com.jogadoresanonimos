package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RegiaoIntergrupoRepository extends JpaRepository<RegiaoIntergrupo, UUID> {

    List<RegiaoIntergrupo> findAllByOrderByOrdemAsc();

    @Query("SELECT DISTINCT r FROM RegiaoIntergrupo r " +
            "LEFT JOIN FETCH r.estados e " +
            "LEFT JOIN FETCH e.grupos g " +
            "LEFT JOIN FETCH g.telefones " +
            "ORDER BY r.ordem ASC")
    List<RegiaoIntergrupo> findAllComHierarquiaCompleta();

    boolean existsByNomeIgnoreCase(String nome);
}

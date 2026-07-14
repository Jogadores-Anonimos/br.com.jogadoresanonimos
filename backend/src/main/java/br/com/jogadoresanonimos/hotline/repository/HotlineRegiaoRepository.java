package br.com.jogadoresanonimos.hotline.repository;

import br.com.jogadoresanonimos.hotline.entity.HotlineRegiao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HotlineRegiaoRepository extends JpaRepository<HotlineRegiao, UUID> {

    @EntityGraph(attributePaths = "telefones")
    List<HotlineRegiao> findAllByOrderByNomeAsc();

    @EntityGraph(attributePaths = "telefones")
    Optional<HotlineRegiao> findById(UUID id);

    boolean existsByNomeIgnoreCase(String nome);
}

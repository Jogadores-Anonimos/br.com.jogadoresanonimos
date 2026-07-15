package br.com.jogadoresanonimos.hotline.repository;

import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.hotline.entity.HotlineRegiao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HotlineRegiaoRepository extends JpaRepository<HotlineRegiao, UUID> {

    @EntityGraph(attributePaths = "telefones")
    List<HotlineRegiao> findAllByOrderByUfAscCidadeAsc();

    @EntityGraph(attributePaths = "telefones")
    @Query("select distinct r from HotlineRegiao r join r.telefones t order by r.uf asc, r.cidade asc")
    List<HotlineRegiao> findAllComPeloMenosUmTelefoneOrderByUfAscCidadeAsc();

    @EntityGraph(attributePaths = "telefones")
    Optional<HotlineRegiao> findById(UUID id);

    boolean existsByUfAndCidadeIgnoreCase(UnidadeFederativa uf, String cidade);
}

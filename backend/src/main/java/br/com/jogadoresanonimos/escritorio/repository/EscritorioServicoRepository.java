package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.EscritorioServico;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EscritorioServicoRepository extends JpaRepository<EscritorioServico, UUID> {

    @EntityGraph(attributePaths = "telefones")
    List<EscritorioServico> findAllByOrderByOrdemAsc();
}

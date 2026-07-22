package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.EscritorioServico;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EscritorioServicoRepository extends JpaRepository<EscritorioServico, UUID> {

    @EntityGraph(attributePaths = "telefones")
    List<EscritorioServico> findAllByOrderByOrdemAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EscritorioServico e WHERE e.id = :id")
    Optional<EscritorioServico> buscarPorIdComLock(UUID id);
}

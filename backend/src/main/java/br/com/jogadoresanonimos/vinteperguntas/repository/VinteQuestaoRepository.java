package br.com.jogadoresanonimos.vinteperguntas.repository;

import br.com.jogadoresanonimos.vinteperguntas.entity.VinteQuestao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VinteQuestaoRepository extends JpaRepository<VinteQuestao, UUID> {

    List<VinteQuestao> findAllByOrderByOrdemAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VinteQuestao v WHERE v.id = :id")
    Optional<VinteQuestao> buscarPorIdComLock(UUID id);
}

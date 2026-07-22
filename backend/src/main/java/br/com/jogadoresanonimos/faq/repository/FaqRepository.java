package br.com.jogadoresanonimos.faq.repository;

import br.com.jogadoresanonimos.faq.entity.Faq;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FaqRepository extends JpaRepository<Faq, UUID> {

    List<Faq> findAllByOrderByOrdemAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Faq f WHERE f.id = :id")
    Optional<Faq> buscarPorIdComLock(UUID id);
}

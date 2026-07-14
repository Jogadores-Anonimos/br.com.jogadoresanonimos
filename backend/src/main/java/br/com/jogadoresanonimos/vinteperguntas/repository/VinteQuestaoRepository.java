package br.com.jogadoresanonimos.vinteperguntas.repository;

import br.com.jogadoresanonimos.vinteperguntas.entity.VinteQuestao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VinteQuestaoRepository extends JpaRepository<VinteQuestao, UUID> {

    List<VinteQuestao> findAllByOrderByOrdemAsc();
}

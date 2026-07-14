package br.com.jogadoresanonimos.reflexao.repository;

import br.com.jogadoresanonimos.reflexao.entity.ReflexaoDia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReflexaoDiaRepository extends JpaRepository<ReflexaoDia, UUID> {

    Optional<ReflexaoDia> findByMesAndDia(int mes, int dia);

    List<ReflexaoDia> findAllByMesOrderByDiaAsc(int mes);
}

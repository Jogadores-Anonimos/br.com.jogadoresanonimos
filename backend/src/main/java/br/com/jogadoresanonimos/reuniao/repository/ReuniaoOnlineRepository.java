package br.com.jogadoresanonimos.reuniao.repository;

import br.com.jogadoresanonimos.reuniao.entity.ReuniaoOnline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReuniaoOnlineRepository extends JpaRepository<ReuniaoOnline, UUID> {

    List<ReuniaoOnline> findAllByOrderByNomeAsc();
}

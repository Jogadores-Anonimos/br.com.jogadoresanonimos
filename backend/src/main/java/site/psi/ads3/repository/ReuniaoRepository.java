package site.psi.ads3.repository;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.psi.ads3.entity.Reuniao;

@Repository
public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {
    List<Reuniao> findByDataHoraBetween(
        LocalDateTime inicio,
        LocalDateTime fim
    );
}
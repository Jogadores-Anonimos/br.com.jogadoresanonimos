package br.com.jogadoresanonimos.reuniao.repository;

import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReuniaoRepository extends JpaRepository<Reuniao, UUID> {

    List<Reuniao> findAllByOrderByNomeAsc();

    List<Reuniao> findAllByCategoriaAndEstadoIsNullAndLatitudeIsNotNullAndLongitudeIsNotNull(CategoriaReuniao categoria);
}

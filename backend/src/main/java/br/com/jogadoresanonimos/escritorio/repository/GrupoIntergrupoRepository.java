package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GrupoIntergrupoRepository extends JpaRepository<GrupoIntergrupo, UUID> {

    List<GrupoIntergrupo> findAllByEstadoIdOrderByOrdemAsc(UUID estadoId);
}

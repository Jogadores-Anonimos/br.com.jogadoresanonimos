package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EstadoIntergrupoRepository extends JpaRepository<EstadoIntergrupo, UUID> {

    List<EstadoIntergrupo> findAllByRegiaoIdOrderByOrdemAsc(UUID regiaoId);

    boolean existsByRegiaoIdAndUf(UUID regiaoId, UnidadeFederativa uf);
}

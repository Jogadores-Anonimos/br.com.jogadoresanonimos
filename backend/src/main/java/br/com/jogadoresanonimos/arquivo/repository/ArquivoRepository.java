package br.com.jogadoresanonimos.arquivo.repository;

import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArquivoRepository extends JpaRepository<Arquivo, UUID> {
    List<Arquivo> findAllByOrderByCriadoEmDesc();
}

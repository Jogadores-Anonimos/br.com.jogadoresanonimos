package br.com.jogadoresanonimos.relato.repository;

import br.com.jogadoresanonimos.relato.entity.Relato;
import br.com.jogadoresanonimos.relato.entity.StatusRelato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RelatoRepository extends JpaRepository<Relato, UUID> {

    Page<Relato> findByStatus(StatusRelato status, Pageable pageable);
}

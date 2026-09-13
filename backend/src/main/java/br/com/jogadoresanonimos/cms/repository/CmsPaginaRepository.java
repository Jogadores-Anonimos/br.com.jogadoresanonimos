package br.com.jogadoresanonimos.cms.repository;

import br.com.jogadoresanonimos.cms.entity.CmsPagina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CmsPaginaRepository extends JpaRepository<CmsPagina, UUID> {

    Optional<CmsPagina> findBySlug(String slug);
}

package br.com.jogadoresanonimos.mural.repository;

import br.com.jogadoresanonimos.mural.entity.BannerMural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface BannerMuralRepository extends JpaRepository<BannerMural, UUID> {

    List<BannerMural> findByAtivoTrue(Sort sort);
}

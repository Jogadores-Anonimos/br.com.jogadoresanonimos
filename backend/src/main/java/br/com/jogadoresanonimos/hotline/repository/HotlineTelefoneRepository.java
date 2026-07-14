package br.com.jogadoresanonimos.hotline.repository;

import br.com.jogadoresanonimos.hotline.entity.HotlineTelefone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HotlineTelefoneRepository extends JpaRepository<HotlineTelefone, UUID> {
}

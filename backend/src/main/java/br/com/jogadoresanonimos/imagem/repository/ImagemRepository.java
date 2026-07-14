package br.com.jogadoresanonimos.imagem.repository;

import br.com.jogadoresanonimos.imagem.entity.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImagemRepository extends JpaRepository<Imagem, UUID> {
}

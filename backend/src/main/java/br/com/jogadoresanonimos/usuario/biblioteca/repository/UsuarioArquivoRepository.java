package br.com.jogadoresanonimos.usuario.biblioteca.repository;

import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.usuario.biblioteca.entity.UsuarioArquivo;
import br.com.jogadoresanonimos.usuario.biblioteca.entity.UsuarioArquivoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface UsuarioArquivoRepository extends JpaRepository<UsuarioArquivo, UsuarioArquivoId> {

    @Query("""
            SELECT a FROM UsuarioArquivo ua
            JOIN Arquivo a ON a.id = ua.id.arquivoId
            WHERE ua.id.usuarioId = :usuarioId
            ORDER BY ua.criadoEm DESC
            """)
    List<Arquivo> listarArquivosDoUsuario(@Param("usuarioId") UUID usuarioId);

    boolean existsById_UsuarioIdAndId_ArquivoId(UUID usuarioId, UUID arquivoId);

    boolean existsById_ArquivoId(UUID arquivoId);

    void deleteById_UsuarioIdAndId_ArquivoId(UUID usuarioId, UUID arquivoId);
}

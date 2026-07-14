package br.com.jogadoresanonimos.usuario.biblioteca.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usuario_arquivo")
public class UsuarioArquivo {

    @EmbeddedId
    private UsuarioArquivoId id;

    private Instant criadoEm;

    protected UsuarioArquivo() {
    }

    public UsuarioArquivo(UUID usuarioId, UUID arquivoId) {
        this.id = new UsuarioArquivoId(usuarioId, arquivoId);
        this.criadoEm = Instant.now();
    }

    public UsuarioArquivoId getId() {
        return id;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}

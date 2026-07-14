package br.com.jogadoresanonimos.usuario.biblioteca.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UsuarioArquivoId implements Serializable {

    private UUID usuarioId;
    private UUID arquivoId;

    protected UsuarioArquivoId() {
    }

    public UsuarioArquivoId(UUID usuarioId, UUID arquivoId) {
        this.usuarioId = usuarioId;
        this.arquivoId = arquivoId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getArquivoId() {
        return arquivoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioArquivoId that)) return false;
        return Objects.equals(usuarioId, that.usuarioId) && Objects.equals(arquivoId, that.arquivoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, arquivoId);
    }
}

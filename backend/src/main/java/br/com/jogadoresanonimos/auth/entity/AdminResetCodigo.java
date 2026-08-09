package br.com.jogadoresanonimos.auth.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_reset_codigo")
public class AdminResetCodigo {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "codigo_hash", nullable = false, unique = true)
    private String codigoHash;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "usado_em")
    private Instant usadoEm;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected AdminResetCodigo() {
    }

    public AdminResetCodigo(UUID usuarioId, String codigoHash, Instant expiraEm) {
        this.usuarioId = usuarioId;
        this.codigoHash = codigoHash;
        this.expiraEm = expiraEm;
        this.criadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getCodigoHash() {
        return codigoHash;
    }

    public Instant getExpiraEm() {
        return expiraEm;
    }

    public Instant getUsadoEm() {
        return usadoEm;
    }

    public void marcarUsado() {
        this.usadoEm = Instant.now();
    }
}

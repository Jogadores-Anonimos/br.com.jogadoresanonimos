package br.com.jogadoresanonimos.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jwt_denylist")
public class JwtDenylistEntry {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "jti", nullable = false, unique = true)
    private String jti;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected JwtDenylistEntry() {
    }

    public JwtDenylistEntry(String jti, Instant expiraEm) {
        this.jti = jti;
        this.expiraEm = expiraEm;
        this.criadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getJti() {
        return jti;
    }

    public Instant getExpiraEm() {
        return expiraEm;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}

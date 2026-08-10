package br.com.jogadoresanonimos.auth.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_confirmacao_token")
public class EmailConfirmacaoToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "usado_em")
    private Instant usadoEm;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEmailConfirmacaoToken tipo;

    @Column(name = "novo_email", length = 255)
    private String novoEmail;

    protected EmailConfirmacaoToken() {
    }

    public EmailConfirmacaoToken(UUID usuarioId, String tokenHash, Instant expiraEm) {
        this(usuarioId, tokenHash, expiraEm, TipoEmailConfirmacaoToken.CADASTRO, null);
    }

    public EmailConfirmacaoToken(UUID usuarioId, String tokenHash, Instant expiraEm,
                                  TipoEmailConfirmacaoToken tipo, String novoEmail) {
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.expiraEm = expiraEm;
        this.criadoEm = Instant.now();
        this.tipo = tipo;
        this.novoEmail = novoEmail;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getTokenHash() {
        return tokenHash;
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

    public boolean expirado() {
        return expiraEm.isBefore(Instant.now());
    }

    public TipoEmailConfirmacaoToken getTipo() {
        return tipo;
    }

    public String getNovoEmail() {
        return novoEmail;
    }
}

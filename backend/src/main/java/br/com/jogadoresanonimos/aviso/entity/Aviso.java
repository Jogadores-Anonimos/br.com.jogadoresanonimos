package br.com.jogadoresanonimos.aviso.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "aviso")
public class Aviso {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String corpo;

    @Column(nullable = false)
    private boolean fixado;

    @Column(name = "data_expiracao")
    private Instant dataExpiracao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Aviso() {
    }

    public Aviso(String titulo, String corpo, boolean fixado, Instant dataExpiracao) {
        this.titulo = titulo;
        this.corpo = corpo;
        this.fixado = fixado;
        this.dataExpiracao = dataExpiracao;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(String titulo, String corpo, boolean fixado, Instant dataExpiracao) {
        this.titulo = titulo;
        this.corpo = corpo;
        this.fixado = fixado;
        this.dataExpiracao = dataExpiracao;
        this.atualizadoEm = Instant.now();
    }

    public boolean isExpirado() {
        return dataExpiracao != null && dataExpiracao.isBefore(Instant.now());
    }

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCorpo() {
        return corpo;
    }

    public boolean isFixado() {
        return fixado;
    }

    public Instant getDataExpiracao() {
        return dataExpiracao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

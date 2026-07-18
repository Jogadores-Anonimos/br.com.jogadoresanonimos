package br.com.jogadoresanonimos.relato.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "relato")
public class Relato {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 10000)
    private String texto;

    @Column(name = "nome_exibicao", length = 30)
    private String nomeExibicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusRelato status;

    @Column(name = "autor_id", nullable = false)
    private UUID autorId;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Relato() {
    }

    public Relato(String texto, String nomeExibicao, UUID autorId) {
        this.texto = texto;
        this.nomeExibicao = nomeExibicao;
        this.autorId = autorId;
        this.status = StatusRelato.pending;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editarTexto(String texto) {
        this.texto = texto;
        this.atualizadoEm = Instant.now();
    }

    public void aprovar() {
        this.status = StatusRelato.approved;
        this.atualizadoEm = Instant.now();
    }

    public void rejeitar() {
        this.status = StatusRelato.rejected;
        this.atualizadoEm = Instant.now();
    }

    public void reverterParaPendente() {
        this.status = StatusRelato.pending;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public StatusRelato getStatus() {
        return status;
    }

    public UUID getAutorId() {
        return autorId;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

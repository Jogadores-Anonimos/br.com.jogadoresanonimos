package br.com.jogadoresanonimos.escritorio.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grupo_intergrupo_telefone")
public class GrupoIntergrupoTelefone {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoIntergrupo grupo;

    @Column(length = 100)
    private String label;

    @Column(nullable = false, length = 100)
    private String telefone;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected GrupoIntergrupoTelefone() {
    }

    public GrupoIntergrupoTelefone(GrupoIntergrupo grupo, String label, String telefone) {
        this.grupo = grupo;
        this.label = label;
        this.telefone = telefone;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public GrupoIntergrupo getGrupo() {
        return grupo;
    }

    public String getLabel() {
        return label;
    }

    public String getTelefone() {
        return telefone;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

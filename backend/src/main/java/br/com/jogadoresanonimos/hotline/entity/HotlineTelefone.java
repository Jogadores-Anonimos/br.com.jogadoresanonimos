package br.com.jogadoresanonimos.hotline.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hotline_telefone")
public class HotlineTelefone {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "regiao_id", nullable = false)
    private HotlineRegiao regiao;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 30)
    private String telefone;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected HotlineTelefone() {
    }

    public HotlineTelefone(HotlineRegiao regiao, String label, String telefone) {
        this.regiao = regiao;
        this.label = label;
        this.telefone = telefone;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(String label, String telefone) {
        this.label = label;
        this.telefone = telefone;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public HotlineRegiao getRegiao() {
        return regiao;
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

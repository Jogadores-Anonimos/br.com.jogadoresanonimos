package br.com.jogadoresanonimos.escritorio.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "escritorio_servico_telefone")
public class EscritorioServicoTelefone {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "escritorio_id", nullable = false)
    private EscritorioServico escritorio;

    @Column(length = 100)
    private String label;

    @Column(nullable = false, length = 100)
    private String telefone;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected EscritorioServicoTelefone() {
    }

    public EscritorioServicoTelefone(EscritorioServico escritorio, String label, String telefone) {
        this.escritorio = escritorio;
        this.label = label;
        this.telefone = telefone;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public EscritorioServico getEscritorio() {
        return escritorio;
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

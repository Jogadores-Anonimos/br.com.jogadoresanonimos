package br.com.jogadoresanonimos.hotline.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hotline_regiao")
public class HotlineRegiao {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100, unique = true)
    private String nome;

    @OneToMany(mappedBy = "regiao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("criadoEm ASC")
    private List<HotlineTelefone> telefones = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected HotlineRegiao() {
    }

    public HotlineRegiao(String nome) {
        this.nome = nome;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void renomear(String nome) {
        this.nome = nome;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<HotlineTelefone> getTelefones() {
        return telefones;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

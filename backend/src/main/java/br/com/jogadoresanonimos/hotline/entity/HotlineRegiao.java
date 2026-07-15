package br.com.jogadoresanonimos.hotline.entity;

import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private UnidadeFederativa uf;

    @Column(nullable = false, length = 150)
    private String cidade;

    @OneToMany(mappedBy = "regiao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("criadoEm ASC")
    private List<HotlineTelefone> telefones = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected HotlineRegiao() {
    }

    public HotlineRegiao(UnidadeFederativa uf, String cidade) {
        this.uf = uf;
        this.cidade = cidade;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(UnidadeFederativa uf, String cidade) {
        this.uf = uf;
        this.cidade = cidade;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UnidadeFederativa getUf() {
        return uf;
    }

    public String getCidade() {
        return cidade;
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

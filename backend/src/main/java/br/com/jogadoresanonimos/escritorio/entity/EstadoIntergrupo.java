package br.com.jogadoresanonimos.escritorio.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "estado_intergrupo")
public class EstadoIntergrupo {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "regiao_id", nullable = false)
    private RegiaoIntergrupo regiao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private UnidadeFederativa uf;

    @Column(nullable = false)
    private int ordem;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<GrupoIntergrupo> grupos = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected EstadoIntergrupo() {
    }

    public EstadoIntergrupo(RegiaoIntergrupo regiao, UnidadeFederativa uf, int ordem) {
        this.regiao = regiao;
        this.uf = uf;
        this.ordem = ordem;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(UnidadeFederativa uf) {
        this.uf = uf;
        this.atualizadoEm = Instant.now();
    }

    public void reordenar(int ordem) {
        this.ordem = ordem;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public RegiaoIntergrupo getRegiao() {
        return regiao;
    }

    public UnidadeFederativa getUf() {
        return uf;
    }

    public int getOrdem() {
        return ordem;
    }

    public List<GrupoIntergrupo> getGrupos() {
        return grupos;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

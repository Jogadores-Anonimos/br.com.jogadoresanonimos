package br.com.jogadoresanonimos.escritorio.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "regiao_intergrupo")
public class RegiaoIntergrupo {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100, unique = true)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, unique = true, updatable = false)
    private RegiaoCodigo codigo;

    @Column(nullable = false)
    private int ordem;

    @OneToMany(mappedBy = "regiao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<EstadoIntergrupo> estados = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected RegiaoIntergrupo() {
    }

    public RegiaoIntergrupo(String nome, RegiaoCodigo codigo, int ordem) {
        this.nome = nome;
        this.codigo = codigo;
        this.ordem = ordem;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(String nome) {
        this.nome = nome;
        this.atualizadoEm = Instant.now();
    }

    public void reordenar(int ordem) {
        this.ordem = ordem;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public RegiaoCodigo getCodigo() {
        return codigo;
    }

    public int getOrdem() {
        return ordem;
    }

    public List<EstadoIntergrupo> getEstados() {
        return estados;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

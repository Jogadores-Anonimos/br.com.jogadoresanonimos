package br.com.jogadoresanonimos.escritorio.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "grupo_intergrupo")
public class GrupoIntergrupo {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoIntergrupo estado;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 300)
    private String endereco;

    @Column(length = 150)
    private String bairro;

    @Column(nullable = false, length = 150)
    private String cidade;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "grupo_intergrupo_dia_semana", joinColumns = @JoinColumn(name = "grupo_id"))
    @Column(name = "dia_semana", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Set<DiaSemana> diasSemana = EnumSet.noneOf(DiaSemana.class);

    @Column(nullable = false, length = 50)
    private String horario;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("criadoEm ASC")
    private List<GrupoIntergrupoTelefone> telefones = new ArrayList<>();

    @Column(nullable = false)
    private int ordem;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected GrupoIntergrupo() {
    }

    public GrupoIntergrupo(EstadoIntergrupo estado, String nome, String endereco, String bairro,
                            String cidade, Set<DiaSemana> diasSemana, String horario, int ordem) {
        this.estado = estado;
        this.nome = nome;
        this.endereco = endereco;
        this.bairro = bairro;
        this.cidade = cidade;
        this.diasSemana = EnumSet.copyOf(diasSemana);
        this.horario = horario;
        this.ordem = ordem;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(String nome, String endereco, String bairro, String cidade,
                        Set<DiaSemana> diasSemana, String horario) {
        this.nome = nome;
        this.endereco = endereco;
        this.bairro = bairro;
        this.cidade = cidade;
        this.diasSemana = EnumSet.copyOf(diasSemana);
        this.horario = horario;
        this.atualizadoEm = Instant.now();
    }

    public void reordenar(int ordem) {
        this.ordem = ordem;
        this.atualizadoEm = Instant.now();
    }

    public void substituirTelefones(List<GrupoIntergrupoTelefone> novosTelefones) {
        this.telefones.clear();
        this.telefones.addAll(novosTelefones);
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public EstadoIntergrupo getEstado() {
        return estado;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public Set<DiaSemana> getDiasSemana() {
        return diasSemana;
    }

    public String getHorario() {
        return horario;
    }

    public List<GrupoIntergrupoTelefone> getTelefones() {
        return telefones;
    }

    public int getOrdem() {
        return ordem;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

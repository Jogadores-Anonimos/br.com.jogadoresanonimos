package br.com.jogadoresanonimos.reuniao.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reuniao")
public class Reuniao {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaReuniao categoria;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false, length = 100)
    private String dia;

    @Column(nullable = false, length = 50)
    private String horario;

    @Column(name = "local", nullable = false, length = 300)
    private String local;

    @Column(length = 20)
    private String whatsapp;

    @Column(name = "dia_semana", length = 20)
    private String diaSemana;

    @Column(length = 20)
    private String periodo;

    private Double latitude;

    private Double longitude;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Reuniao() {
    }

    public Reuniao(String nome, CategoriaReuniao categoria, String descricao, String dia, String horario,
                    String local, String whatsapp, String diaSemana, String periodo,
                    Double latitude, Double longitude) {
        this.nome = nome;
        this.categoria = categoria;
        this.descricao = descricao;
        this.dia = dia;
        this.horario = horario;
        this.local = local;
        this.whatsapp = whatsapp;
        this.diaSemana = diaSemana;
        this.periodo = periodo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(String nome, CategoriaReuniao categoria, String descricao, String dia, String horario,
                        String local, String whatsapp, String diaSemana, String periodo,
                        Double latitude, Double longitude) {
        this.nome = nome;
        this.categoria = categoria;
        this.descricao = descricao;
        this.dia = dia;
        this.horario = horario;
        this.local = local;
        this.whatsapp = whatsapp;
        this.diaSemana = diaSemana;
        this.periodo = periodo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public CategoriaReuniao getCategoria() {
        return categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDia() {
        return dia;
    }

    public String getHorario() {
        return horario;
    }

    public String getLocal() {
        return local;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public String getPeriodo() {
        return periodo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

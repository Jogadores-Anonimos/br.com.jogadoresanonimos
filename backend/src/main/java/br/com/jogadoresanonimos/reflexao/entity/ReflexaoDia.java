package br.com.jogadoresanonimos.reflexao.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reflexao_dia")
public class ReflexaoDia {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private int mes;

    @Column(nullable = false)
    private int dia;

    @Column(nullable = false, length = 2000)
    private String texto;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected ReflexaoDia() {
    }

    public ReflexaoDia(int mes, int dia, String texto) {
        this.mes = mes;
        this.dia = dia;
        this.texto = texto;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(String texto) {
        this.texto = texto;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public int getMes() {
        return mes;
    }

    public int getDia() {
        return dia;
    }

    public String getTexto() {
        return texto;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

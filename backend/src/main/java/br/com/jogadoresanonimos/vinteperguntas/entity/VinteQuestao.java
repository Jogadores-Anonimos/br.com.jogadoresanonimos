package br.com.jogadoresanonimos.vinteperguntas.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vinte_questao")
public class VinteQuestao {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private int ordem;

    @Column(nullable = false, length = 500)
    private String texto;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Column(name = "atualizado_por", nullable = false)
    private UUID atualizadoPor;

    protected VinteQuestao() {
    }

    public VinteQuestao(int ordem, String texto, UUID atualizadoPor) {
        this.ordem = ordem;
        this.texto = texto;
        this.atualizadoPor = atualizadoPor;
        this.atualizadoEm = Instant.now();
    }

    public void atualizarTexto(String texto, UUID atualizadoPor) {
        this.texto = texto;
        this.atualizadoPor = atualizadoPor;
        this.atualizadoEm = Instant.now();
    }

    public void reordenar(int ordem) {
        this.ordem = ordem;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public int getOrdem() {
        return ordem;
    }

    public String getTexto() {
        return texto;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public UUID getAtualizadoPor() {
        return atualizadoPor;
    }
}

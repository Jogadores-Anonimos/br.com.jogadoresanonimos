package br.com.jogadoresanonimos.faq.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "faq")
public class Faq {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 500)
    private String pergunta;

    @Column(nullable = false, length = 4000)
    private String resposta;

    @Column(nullable = false)
    private int ordem;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Faq() {
    }

    public Faq(String pergunta, String resposta, int ordem) {
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.ordem = ordem;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(String pergunta, String resposta) {
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.atualizadoEm = Instant.now();
    }

    public void reordenar(int ordem) {
        this.ordem = ordem;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getPergunta() {
        return pergunta;
    }

    public String getResposta() {
        return resposta;
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

package br.com.jogadoresanonimos.imagem.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "imagem")
public class Imagem {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "nome_original", nullable = false)
    private String nomeOriginal;

    @Column(name = "chave_arquivo", nullable = false, unique = true)
    private String chaveArquivo;

    @Column(name = "tamanho_bytes", nullable = false)
    private long tamanhoBytes;

    @Column(nullable = false, length = 300)
    private String alt;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "criado_por", nullable = false)
    private UUID criadoPor;

    protected Imagem() {
    }

    public Imagem(String nomeOriginal, String chaveArquivo, long tamanhoBytes, String alt, UUID criadoPor) {
        this.nomeOriginal = nomeOriginal;
        this.chaveArquivo = chaveArquivo;
        this.tamanhoBytes = tamanhoBytes;
        this.alt = alt;
        this.criadoPor = criadoPor;
        this.criadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public String getChaveArquivo() {
        return chaveArquivo;
    }

    public long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public String getAlt() {
        return alt;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public UUID getCriadoPor() {
        return criadoPor;
    }
}

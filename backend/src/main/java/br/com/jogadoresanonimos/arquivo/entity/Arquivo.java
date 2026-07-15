package br.com.jogadoresanonimos.arquivo.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "arquivo")
public class Arquivo {

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
    private String titulo;

    @Column(length = 500)
    private String descricao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "criado_por", nullable = false)
    private UUID criadoPor;

    @Column(name = "thumbnail_chave")
    private String thumbnailChave;

    protected Arquivo() {
    }

    public Arquivo(String nomeOriginal, String chaveArquivo, long tamanhoBytes, String titulo, String descricao, UUID criadoPor) {
        this.nomeOriginal = nomeOriginal;
        this.chaveArquivo = chaveArquivo;
        this.tamanhoBytes = tamanhoBytes;
        this.titulo = titulo;
        this.descricao = descricao;
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

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public UUID getCriadoPor() {
        return criadoPor;
    }

    public void editarMetadados(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getThumbnailChave() {
        return thumbnailChave;
    }

    public void setThumbnailChave(String thumbnailChave) {
        this.thumbnailChave = thumbnailChave;
    }
}

package br.com.jogadoresanonimos.mural.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "banner_mural")
public class BannerMural {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "imagem_id")
    private UUID imagemId;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "alt_text", nullable = false, length = 300)
    private String altText;

    @Column(length = 200)
    private String legenda;

    @Column(name = "link_destino", length = 500)
    private String linkDestino;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected BannerMural() {
    }

    public BannerMural(UUID imagemId, String videoUrl, String altText, String legenda, String linkDestino) {
        this.imagemId = imagemId;
        this.videoUrl = videoUrl;
        this.altText = altText;
        this.legenda = legenda;
        this.linkDestino = linkDestino;
        this.ativo = true;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(UUID imagemId, String videoUrl, String altText, String legenda, String linkDestino, boolean ativo) {
        this.imagemId = imagemId;
        this.videoUrl = videoUrl;
        this.altText = altText;
        this.legenda = legenda;
        this.linkDestino = linkDestino;
        this.ativo = ativo;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getImagemId() {
        return imagemId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getAltText() {
        return altText;
    }

    public String getLegenda() {
        return legenda;
    }

    public String getLinkDestino() {
        return linkDestino;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

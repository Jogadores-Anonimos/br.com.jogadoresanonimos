package br.com.jogadoresanonimos.cms.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "cms_bloco")
public class CmsBloco {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagina_id", nullable = false)
    private CmsPagina pagina;

    @Column(nullable = false)
    private int ordem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoBloco tipo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "imagem_id")
    private UUID imagemId;

    @Column(name = "arquivo_id")
    private UUID arquivoId;

    @Column(length = 300)
    private String alt;

    protected CmsBloco() {
    }

    public CmsBloco(CmsPagina pagina, int ordem, TipoBloco tipo, String conteudo, UUID imagemId, String alt) {
        this(pagina, ordem, tipo, conteudo, imagemId, null, alt);
    }

    public CmsBloco(CmsPagina pagina, int ordem, TipoBloco tipo, String conteudo, UUID imagemId, UUID arquivoId, String alt) {
        this.pagina = pagina;
        this.ordem = ordem;
        this.tipo = tipo;
        this.conteudo = conteudo;
        this.imagemId = imagemId;
        this.arquivoId = arquivoId;
        this.alt = alt;
    }

    public UUID getId() {
        return id;
    }

    public int getOrdem() {
        return ordem;
    }

    public TipoBloco getTipo() {
        return tipo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public UUID getImagemId() {
        return imagemId;
    }

    public UUID getArquivoId() {
        return arquivoId;
    }

    public String getAlt() {
        return alt;
    }
}

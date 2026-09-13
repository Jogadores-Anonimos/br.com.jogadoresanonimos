package br.com.jogadoresanonimos.reuniao.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reuniao_online")
public class ReuniaoOnline {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Column(length = 2000)
    private String descricao;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "panfleto_imagem_id")
    private UUID panfletoImagemId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reuniao_online_horario", joinColumns = @JoinColumn(name = "reuniao_online_id"))
    private List<AgendaItemReuniaoOnline> agenda = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected ReuniaoOnline() {
    }

    public ReuniaoOnline(String nome, String telefone, String descricao, String videoUrl, UUID panfletoImagemId,
                          List<AgendaItemReuniaoOnline> agenda) {
        this.nome = nome;
        this.telefone = telefone;
        this.descricao = descricao;
        this.videoUrl = videoUrl;
        this.panfletoImagemId = panfletoImagemId;
        this.agenda = new ArrayList<>(agenda);
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(String nome, String telefone, String descricao, String videoUrl, UUID panfletoImagemId,
                        List<AgendaItemReuniaoOnline> agenda) {
        this.nome = nome;
        this.telefone = telefone;
        this.descricao = descricao;
        this.videoUrl = videoUrl;
        this.panfletoImagemId = panfletoImagemId;
        this.agenda.clear();
        this.agenda.addAll(agenda);
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public UUID getPanfletoImagemId() {
        return panfletoImagemId;
    }

    public List<AgendaItemReuniaoOnline> getAgenda() {
        return agenda;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}

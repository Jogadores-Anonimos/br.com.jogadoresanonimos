package br.com.jogadoresanonimos.cms.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cms_pagina")
public class CmsPagina {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Column(name = "atualizado_por", nullable = false)
    private UUID atualizadoPor;

    @Column(name = "oculta_na_listagem_admin", nullable = false)
    private boolean ocultaNaListagemAdmin = false;

    @OneToMany(mappedBy = "pagina", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<CmsBloco> blocos = new ArrayList<>();

    protected CmsPagina() {
    }

    public CmsPagina(String slug, String titulo, UUID atualizadoPor) {
        this.slug = slug;
        this.titulo = titulo;
        this.atualizadoPor = atualizadoPor;
        this.atualizadoEm = Instant.now();
    }

    public void atualizar(UUID atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
        this.atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitulo() {
        return titulo;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public UUID getAtualizadoPor() {
        return atualizadoPor;
    }

    public boolean isOcultaNaListagemAdmin() {
        return ocultaNaListagemAdmin;
    }

    public List<CmsBloco> getBlocos() {
        return blocos;
    }
}

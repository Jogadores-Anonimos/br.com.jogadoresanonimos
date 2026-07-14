package br.com.jogadoresanonimos.escritorio.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "escritorio_servico")
public class EscritorioServico {

    private static final String SEPARADOR_EMAILS = ";";

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 300)
    private String endereco;

    @Column(length = 150)
    private String complemento;

    @Column(length = 150)
    private String bairro;

    @Column(nullable = false, length = 150)
    private String cidade;

    @Column(length = 500)
    private String emails;

    @OneToMany(mappedBy = "escritorio", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("criadoEm ASC")
    private List<EscritorioServicoTelefone> telefones = new ArrayList<>();

    @Column(nullable = false)
    private int ordem;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected EscritorioServico() {
    }

    public EscritorioServico(String nome, String endereco, String complemento, String bairro, String cidade,
                              List<String> emails, int ordem) {
        this.nome = nome;
        this.endereco = endereco;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.emails = juntarEmails(emails);
        this.ordem = ordem;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public void editar(String nome, String endereco, String complemento, String bairro, String cidade,
                        List<String> emails) {
        this.nome = nome;
        this.endereco = endereco;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.emails = juntarEmails(emails);
        this.atualizadoEm = Instant.now();
    }

    public void reordenar(int ordem) {
        this.ordem = ordem;
        this.atualizadoEm = Instant.now();
    }

    public void substituirTelefones(List<EscritorioServicoTelefone> novosTelefones) {
        this.telefones.clear();
        this.telefones.addAll(novosTelefones);
        this.atualizadoEm = Instant.now();
    }

    private static String juntarEmails(List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return null;
        }
        return String.join(SEPARADOR_EMAILS, emails);
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public List<EscritorioServicoTelefone> getTelefones() {
        return telefones;
    }

    public List<String> getEmails() {
        if (emails == null || emails.isBlank()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(List.of(emails.split(SEPARADOR_EMAILS)));
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

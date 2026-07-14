package br.com.jogadoresanonimos.usuario.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String apelido;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "senha_alterada_em", nullable = false)
    private Instant senhaAlteradaEm;

    protected Usuario() {
    }

    public Usuario(String apelido, String email, String senhaHash, Role role) {
        this.apelido = apelido;
        this.email = email;
        this.senhaHash = senhaHash;
        this.role = role;
        this.criadoEm = Instant.now();
        this.senhaAlteradaEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getApelido() {
        return apelido;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
        this.senhaAlteradaEm = Instant.now();
    }

    public Role getRole() {
        return role;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getSenhaAlteradaEm() {
        return senhaAlteradaEm;
    }

    public void alterarRole(Role novaRole) {
        this.role = novaRole;
    }

    public void editarConta(String novoApelido, String novoEmail) {
        this.apelido = novoApelido;
        this.email = novoEmail;
    }
}

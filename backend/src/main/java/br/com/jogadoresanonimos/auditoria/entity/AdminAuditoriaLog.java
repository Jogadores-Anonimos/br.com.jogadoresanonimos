package br.com.jogadoresanonimos.auditoria.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_auditoria_log")
public class AdminAuditoriaLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @Column(nullable = false, length = 50)
    private String entidade;

    @Column(name = "entidade_id", nullable = false)
    private UUID entidadeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AcaoAuditoria acao;

    @Column(name = "status_anterior", length = 20)
    private String statusAnterior;

    @Column(name = "status_novo", nullable = false, length = 20)
    private String statusNovo;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected AdminAuditoriaLog() {
    }

    public AdminAuditoriaLog(UUID adminId, String entidade, UUID entidadeId, AcaoAuditoria acao,
                              String statusAnterior, String statusNovo) {
        this.adminId = adminId;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.acao = acao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.criadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAdminId() {
        return adminId;
    }

    public String getEntidade() {
        return entidade;
    }

    public UUID getEntidadeId() {
        return entidadeId;
    }

    public AcaoAuditoria getAcao() {
        return acao;
    }

    public String getStatusAnterior() {
        return statusAnterior;
    }

    public String getStatusNovo() {
        return statusNovo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}

package br.com.jogadoresanonimos.cms.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cms_alteracao_log")
public class CmsAlteracaoLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "pagina_id", nullable = false)
    private UUID paginaId;

    @Column(name = "alterado_em", nullable = false)
    private Instant alteradoEm;

    @Column(name = "alterado_por", nullable = false)
    private UUID alteradoPor;

    @Column(name = "snapshot_anterior", columnDefinition = "TEXT", nullable = false)
    private String snapshotAnterior;

    protected CmsAlteracaoLog() {
    }

    public CmsAlteracaoLog(UUID paginaId, UUID alteradoPor, String snapshotAnterior) {
        this.paginaId = paginaId;
        this.alteradoPor = alteradoPor;
        this.snapshotAnterior = snapshotAnterior;
        this.alteradoEm = Instant.now();
    }
}

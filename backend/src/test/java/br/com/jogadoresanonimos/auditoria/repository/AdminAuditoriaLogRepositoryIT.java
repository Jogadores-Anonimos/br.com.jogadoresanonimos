package br.com.jogadoresanonimos.auditoria.repository;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.entity.AdminAuditoriaLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AdminAuditoriaLogRepositoryIT {

    @Autowired
    private AdminAuditoriaLogRepository repository;

    @Test
    void buscaSemFiltrosNaoLancaExcecaoENaoDependeDeTipoDoParametro() {
        repository.save(new AdminAuditoriaLog(UUID.randomUUID(), "relato", UUID.randomUUID(),
                AcaoAuditoria.APROVAR, "pending", "approved"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdminAuditoriaLog> resultado = repository.findAll(
                AdminAuditoriaLogSpecifications.comFiltros(null, null, null, null), pageable);
        assertThat(resultado).isNotEmpty();
    }

    @Test
    void buscaComTodosOsFiltrosPreenchidos() {
        UUID entidadeId = UUID.randomUUID();
        repository.save(new AdminAuditoriaLog(UUID.randomUUID(), "relato", entidadeId,
                AcaoAuditoria.APROVAR, "pending", "approved"));
        Instant agora = Instant.now();
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdminAuditoriaLog> resultado = repository.findAll(
                AdminAuditoriaLogSpecifications.comFiltros(
                        "relato", AcaoAuditoria.APROVAR,
                        agora.minus(1, ChronoUnit.HOURS), agora.plus(1, ChronoUnit.HOURS)),
                pageable);
        assertThat(resultado).isNotEmpty();
        assertThat(resultado.getContent().get(0).getEntidadeId()).isEqualTo(entidadeId);
    }

    @Test
    void buscaComApenasDataInicioPreenchida() {
        repository.save(new AdminAuditoriaLog(UUID.randomUUID(), "reuniao", UUID.randomUUID(),
                AcaoAuditoria.REJEITAR, "pending", "rejected"));
        Instant ontem = Instant.now().minus(1, ChronoUnit.DAYS);
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdminAuditoriaLog> resultado = repository.findAll(
                AdminAuditoriaLogSpecifications.comFiltros(null, null, ontem, null), pageable);
        assertThat(resultado).isNotEmpty();
    }

    @Test
    void buscaComApenasDataFimPreenchida() {
        repository.save(new AdminAuditoriaLog(UUID.randomUUID(), "reuniao", UUID.randomUUID(),
                AcaoAuditoria.REJEITAR, "pending", "rejected"));
        Instant amanha = Instant.now().plus(1, ChronoUnit.DAYS);
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdminAuditoriaLog> resultado = repository.findAll(
                AdminAuditoriaLogSpecifications.comFiltros(null, null, null, amanha), pageable);
        assertThat(resultado).isNotEmpty();
    }

    @Test
    void buscaComApenasEntidadeEAcaoPreenchidas() {
        repository.save(new AdminAuditoriaLog(UUID.randomUUID(), "aviso", UUID.randomUUID(),
                AcaoAuditoria.REMOVER, "pending", "rejected"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdminAuditoriaLog> resultado = repository.findAll(
                AdminAuditoriaLogSpecifications.comFiltros("aviso", AcaoAuditoria.REMOVER, null, null),
                pageable);
        assertThat(resultado).isNotEmpty();
    }
}

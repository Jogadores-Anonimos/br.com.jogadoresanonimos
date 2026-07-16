package br.com.jogadoresanonimos.auditoria.service;

import br.com.jogadoresanonimos.auditoria.repository.AdminAuditoriaLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class AuditoriaLogGcJob {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaLogGcJob.class);

    private final AdminAuditoriaLogRepository adminAuditoriaLogRepository;
    private final int retencaoDias;

    public AuditoriaLogGcJob(AdminAuditoriaLogRepository adminAuditoriaLogRepository,
                              @Value("${app.auditoria.retencao-dias:730}") int retencaoDias) {
        this.adminAuditoriaLogRepository = adminAuditoriaLogRepository;
        this.retencaoDias = retencaoDias;
    }

    @Scheduled(cron = "0 0 5 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void executar() {
        Instant limite = Instant.now().minus(retencaoDias, ChronoUnit.DAYS);
        long removidos = adminAuditoriaLogRepository.deleteByCriadoEmBefore(limite);
        log.info("gc_auditoria_log_executado removidos={}", removidos);
    }
}

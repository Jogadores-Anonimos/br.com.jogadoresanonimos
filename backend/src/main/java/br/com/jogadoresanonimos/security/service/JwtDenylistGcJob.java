package br.com.jogadoresanonimos.security.service;

import br.com.jogadoresanonimos.security.repository.JwtDenylistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class JwtDenylistGcJob {

    private static final Logger log = LoggerFactory.getLogger(JwtDenylistGcJob.class);

    private final JwtDenylistRepository jwtDenylistRepository;

    public JwtDenylistGcJob(JwtDenylistRepository jwtDenylistRepository) {
        this.jwtDenylistRepository = jwtDenylistRepository;
    }

    @Scheduled(cron = "0 30 5 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void executar() {
        long removidos = jwtDenylistRepository.deleteByExpiraEmBefore(Instant.now());
        log.info("gc_jwt_denylist_executado removidos={}", removidos);
    }
}

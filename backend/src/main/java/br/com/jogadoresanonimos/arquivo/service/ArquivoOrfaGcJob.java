package br.com.jogadoresanonimos.arquivo.service;

import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ArquivoOrfaGcJob {

    private static final Logger log = LoggerFactory.getLogger(ArquivoOrfaGcJob.class);
    private static final Duration PERIODO_DE_GRACA = Duration.ofDays(7);

    private final ArquivoRepository arquivoRepository;
    private final StorageClient storageClient;

    public ArquivoOrfaGcJob(ArquivoRepository arquivoRepository, StorageClient storageClient) {
        this.arquivoRepository = arquivoRepository;
        this.storageClient = storageClient;
    }

    @Scheduled(cron = "0 30 4 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void executar() {
        Instant limite = Instant.now().minus(PERIODO_DE_GRACA);
        List<UUID> candidatosIds = arquivoRepository.buscarIdsOrfaosAnterioresA(limite);
        List<Arquivo> candidatos = arquivoRepository.findAllById(candidatosIds);
        int removidos = 0;
        for (Arquivo arquivo : candidatos) {
            storageClient.delete(arquivo.getChaveArquivo());
            arquivoRepository.delete(arquivo);
            removidos++;
        }
        log.info("gc_arquivo_orfao_executado removidos={}", removidos);
    }
}

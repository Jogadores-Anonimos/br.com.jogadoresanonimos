package br.com.jogadoresanonimos.arquivo.service;

import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
    public void executar() {
        Instant limite = Instant.now().minus(PERIODO_DE_GRACA);
        List<UUID> candidatosIds = arquivoRepository.buscarIdsOrfaosAnterioresA(limite);
        List<Object[]> idsEChaves = arquivoRepository.findIdEChavesPorIdIn(candidatosIds);
        List<UUID> idsRemovidos = new ArrayList<>();
        for (Object[] idEChaves : idsEChaves) {
            UUID id = (UUID) idEChaves[0];
            String chaveArquivo = (String) idEChaves[1];
            String thumbnailChave = (String) idEChaves[2];
            storageClient.delete(chaveArquivo);
            if (thumbnailChave != null) {
                storageClient.delete(thumbnailChave);
            }
            idsRemovidos.add(id);
        }
        if (!idsRemovidos.isEmpty()) {
            arquivoRepository.deleteAllByIdInBatch(idsRemovidos);
        }
        log.info("gc_arquivo_orfao_executado removidos={}", idsRemovidos.size());
    }
}

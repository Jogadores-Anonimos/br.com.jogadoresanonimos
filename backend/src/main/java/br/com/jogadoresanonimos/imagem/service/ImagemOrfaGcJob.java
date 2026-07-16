package br.com.jogadoresanonimos.imagem.service;

import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
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
public class ImagemOrfaGcJob {

    private static final Logger log = LoggerFactory.getLogger(ImagemOrfaGcJob.class);
    private static final Duration PERIODO_DE_GRACA = Duration.ofDays(7);

    private final ImagemRepository imagemRepository;
    private final StorageClient storageClient;

    public ImagemOrfaGcJob(ImagemRepository imagemRepository, StorageClient storageClient) {
        this.imagemRepository = imagemRepository;
        this.storageClient = storageClient;
    }

    @Scheduled(cron = "0 0 4 * * *", zone = "America/Sao_Paulo")
    public void executar() {
        Instant limite = Instant.now().minus(PERIODO_DE_GRACA);
        List<UUID> candidatasIds = imagemRepository.buscarIdsOrfaasAnterioresA(limite);
        List<Object[]> idsEChaves = imagemRepository.findIdEChavePorIdIn(candidatasIds);
        List<UUID> idsRemovidas = new ArrayList<>();
        for (Object[] idEChave : idsEChaves) {
            UUID id = (UUID) idEChave[0];
            String chaveArquivo = (String) idEChave[1];
            storageClient.delete(chaveArquivo);
            idsRemovidas.add(id);
        }
        if (!idsRemovidas.isEmpty()) {
            imagemRepository.deleteAllByIdInBatch(idsRemovidas);
        }
        log.info("gc_imagem_orfa_executado removidas={}", idsRemovidas.size());
    }
}

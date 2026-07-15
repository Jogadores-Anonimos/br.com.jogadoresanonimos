package br.com.jogadoresanonimos.imagem.service;

import br.com.jogadoresanonimos.imagem.entity.Imagem;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
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
    @Transactional
    public void executar() {
        Instant limite = Instant.now().minus(PERIODO_DE_GRACA);
        List<UUID> candidatasIds = imagemRepository.buscarIdsOrfaasAnterioresA(limite);
        List<Imagem> candidatas = imagemRepository.findAllById(candidatasIds);
        int removidas = 0;
        for (Imagem imagem : candidatas) {
            storageClient.delete(imagem.getChaveArquivo());
            imagemRepository.delete(imagem);
            removidas++;
        }
        log.info("gc_imagem_orfa_executado removidas={}", removidas);
    }
}

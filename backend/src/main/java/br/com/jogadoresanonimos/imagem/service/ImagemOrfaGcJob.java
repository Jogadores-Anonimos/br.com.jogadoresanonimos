package br.com.jogadoresanonimos.imagem.service;

import br.com.jogadoresanonimos.cms.repository.CmsBlocoRepository;
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

/**
 * GC diário de imagem órfã (ver decisão D3 do devops): remove upload sem
 * cms_bloco referenciando após período de graça de 7 dias. Não cobre o
 * caso (a) do contrato (objeto em disco sem linha em `imagem`, ex. insert
 * que falhou após upload) porque hoje não há listagem paginada do
 * diretório de storage implementada — se necessário, adicionar rotina
 * separada de varredura do diretório local (LocalDiskStorageClient).
 */
@Component
public class ImagemOrfaGcJob {

    private static final Logger log = LoggerFactory.getLogger(ImagemOrfaGcJob.class);
    private static final Duration PERIODO_DE_GRACA = Duration.ofDays(7);

    private final ImagemRepository imagemRepository;
    private final CmsBlocoRepository cmsBlocoRepository;
    private final StorageClient storageClient;

    public ImagemOrfaGcJob(ImagemRepository imagemRepository, CmsBlocoRepository cmsBlocoRepository,
                            StorageClient storageClient) {
        this.imagemRepository = imagemRepository;
        this.cmsBlocoRepository = cmsBlocoRepository;
        this.storageClient = storageClient;
    }

    @Scheduled(cron = "0 0 4 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void executar() {
        Instant limite = Instant.now().minus(PERIODO_DE_GRACA);
        List<Imagem> candidatas = imagemRepository.findAll().stream()
                .filter(img -> img.getCriadoEm().isBefore(limite))
                .filter(img -> !cmsBlocoRepository.existsByImagemId(img.getId()))
                .toList();

        int removidas = 0;
        for (Imagem imagem : candidatas) {
            storageClient.delete(imagem.getChaveArquivo());
            imagemRepository.delete(imagem);
            removidas++;
        }
        log.info("gc_imagem_orfa_executado removidas={}", removidas);
    }
}

package br.com.jogadoresanonimos.arquivo.service;

import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.cms.repository.CmsBlocoRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * GC diário de arquivo (PDF de literatura) órfão — mesmo padrão e mesmo
 * período de graça do ImagemOrfaGcJob (ver decisão D3 devops), reaproveitado
 * 1:1 porque upload dissociado + GC de 7 dias já é a política estabelecida
 * para qualquer mídia administrada pelo CMS, não é decisão nova.
 */
@Component
public class ArquivoOrfaGcJob {

    private static final Logger log = LoggerFactory.getLogger(ArquivoOrfaGcJob.class);
    private static final Duration PERIODO_DE_GRACA = Duration.ofDays(7);

    private final ArquivoRepository arquivoRepository;
    private final CmsBlocoRepository cmsBlocoRepository;
    private final StorageClient storageClient;

    public ArquivoOrfaGcJob(ArquivoRepository arquivoRepository, CmsBlocoRepository cmsBlocoRepository,
                             StorageClient storageClient) {
        this.arquivoRepository = arquivoRepository;
        this.cmsBlocoRepository = cmsBlocoRepository;
        this.storageClient = storageClient;
    }

    @Scheduled(cron = "0 30 4 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void executar() {
        Instant limite = Instant.now().minus(PERIODO_DE_GRACA);
        List<Arquivo> candidatos = arquivoRepository.findAll().stream()
                .filter(a -> a.getCriadoEm().isBefore(limite))
                .filter(a -> !cmsBlocoRepository.existsByArquivoId(a.getId()))
                .toList();

        int removidos = 0;
        for (Arquivo arquivo : candidatos) {
            storageClient.delete(arquivo.getChaveArquivo());
            arquivoRepository.delete(arquivo);
            removidos++;
        }
        log.info("gc_arquivo_orfao_executado removidos={}", removidos);
    }
}

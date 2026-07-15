package br.com.jogadoresanonimos.imagem.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.cms.repository.CmsBlocoRepository;
import br.com.jogadoresanonimos.imagem.dto.ImagemResponse;
import br.com.jogadoresanonimos.imagem.entity.Imagem;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class ImagemService {

    private static final long LIMITE_BYTES = 5L * 1024 * 1024;

    private final ImagemRepository imagemRepository;
    private final CmsBlocoRepository cmsBlocoRepository;
    private final ImagemProcessor imagemProcessor;
    private final StorageClient storageClient;
    private final RateLimiterService rateLimiterService;

    public ImagemService(ImagemRepository imagemRepository, CmsBlocoRepository cmsBlocoRepository,
                          ImagemProcessor imagemProcessor, StorageClient storageClient,
                          RateLimiterService rateLimiterService) {
        this.imagemRepository = imagemRepository;
        this.cmsBlocoRepository = cmsBlocoRepository;
        this.imagemProcessor = imagemProcessor;
        this.storageClient = storageClient;
        this.rateLimiterService = rateLimiterService;
    }

    @Transactional
    public ImagemResponse upload(MultipartFile arquivo, String alt, UUID adminId) {
        rateLimiterService.check("upload-imagem:" + adminId, 10, Duration.ofMinutes(1));
        rateLimiterService.check("upload-imagem:global", 60, Duration.ofMinutes(1));
        if (alt == null || alt.isBlank()) {
            throw new ValidationException("Texto alternativo (alt) é obrigatório");
        }
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidationException("Arquivo é obrigatório");
        }
        if (arquivo.getSize() > LIMITE_BYTES) {
            throw new ValidationException("Arquivo excede o tamanho máximo permitido (5MB)");
        }
        byte[] original;
        try {
            original = arquivo.getBytes();
        } catch (IOException e) {
            throw new ValidationException("Não foi possível ler o arquivo enviado");
        }
        ImagemProcessor.ImagemProcessada processada = imagemProcessor.processar(original);
        String chave = UUID.randomUUID() + "." + processada.extensao();
        storageClient.upload(chave, processada.bytes(), processada.contentType());
        Imagem imagem = new Imagem(arquivo.getOriginalFilename(), chave, processada.bytes().length, alt, adminId);
        imagem = imagemRepository.save(imagem);
        return ImagemResponse.from(imagem, storageClient.urlPublicaPara(chave));
    }

    @Transactional
    public void remover(UUID id) {
        Imagem imagem = imagemRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Imagem não encontrada"));
        boolean referenciada = cmsBlocoRepository.existsByImagemId(id);
        if (referenciada) {
            throw new ConflitoDeEstadoException("Imagem ainda está em uso em uma página e não pode ser removida");
        }
        storageClient.delete(imagem.getChaveArquivo());
        imagemRepository.delete(imagem);
    }
}

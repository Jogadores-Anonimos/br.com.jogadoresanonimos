package br.com.jogadoresanonimos.arquivo.service;

import br.com.jogadoresanonimos.arquivo.dto.ArquivoResponse;
import br.com.jogadoresanonimos.arquivo.dto.EditarArquivoRequest;
import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.cms.repository.CmsBlocoRepository;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class ArquivoService {

    private static final long LIMITE_BYTES = 20L * 1024 * 1024;

    private final ArquivoRepository arquivoRepository;
    private final CmsBlocoRepository cmsBlocoRepository;
    private final ArquivoProcessor arquivoProcessor;
    private final StorageClient storageClient;
    private final RateLimiterService rateLimiterService;

    public ArquivoService(ArquivoRepository arquivoRepository, CmsBlocoRepository cmsBlocoRepository,
                           ArquivoProcessor arquivoProcessor, StorageClient storageClient,
                           RateLimiterService rateLimiterService) {
        this.arquivoRepository = arquivoRepository;
        this.cmsBlocoRepository = cmsBlocoRepository;
        this.arquivoProcessor = arquivoProcessor;
        this.storageClient = storageClient;
        this.rateLimiterService = rateLimiterService;
    }

    @Transactional
    public ArquivoResponse upload(MultipartFile arquivo, String titulo, String descricao, UUID adminId) {
        rateLimiterService.check("upload-arquivo:" + adminId, 10, Duration.ofMinutes(1));
        rateLimiterService.check("upload-arquivo:global", 60, Duration.ofMinutes(1));

        if (titulo == null || titulo.isBlank()) {
            throw new ValidationException("Título de exibição é obrigatório");
        }
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidationException("Arquivo é obrigatório");
        }
        if (arquivo.getSize() > LIMITE_BYTES) {
            throw new ValidationException("Arquivo excede o tamanho máximo permitido (20MB)");
        }

        byte[] original;
        try {
            original = arquivo.getBytes();
        } catch (IOException e) {
            throw new ValidationException("Não foi possível ler o arquivo enviado");
        }
        if (original.length > LIMITE_BYTES) {
            throw new ValidationException("Arquivo excede o tamanho máximo permitido (20MB)");
        }

        ArquivoProcessor.ArquivoProcessado processado = arquivoProcessor.processar(original);
        String chave = UUID.randomUUID() + "." + processado.extensao();

        storageClient.upload(chave, processado.bytes(), processado.contentType());

        Arquivo entidade = new Arquivo(arquivo.getOriginalFilename(), chave, processado.bytes().length, titulo,
                descricao, adminId);

        if (processado.thumbnailPng() != null) {
            String chaveThumbnail = "thumb-" + UUID.randomUUID() + ".png";
            storageClient.upload(chaveThumbnail, processado.thumbnailPng(), "image/png");
            entidade.setThumbnailChave(chaveThumbnail);
        }

        entidade = arquivoRepository.save(entidade);

        return paraResponse(entidade);
    }

    @Transactional
    public ArquivoResponse editar(UUID id, EditarArquivoRequest request) {
        Arquivo arquivo = arquivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Arquivo não encontrado"));

        String titulo = request.titulo() == null ? null : request.titulo().trim();
        if (titulo == null || titulo.isBlank()) {
            throw new ValidationException("Título de exibição é obrigatório");
        }
        String descricao = request.descricao() == null ? null : request.descricao().trim();

        arquivo.editarMetadados(titulo, descricao);
        return paraResponse(arquivo);
    }

    @Transactional
    public void remover(UUID id) {
        Arquivo arquivo = arquivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Arquivo não encontrado"));

        boolean referenciado = cmsBlocoRepository.existsByArquivoId(id);
        if (referenciado) {
            throw new ConflitoDeEstadoException("Arquivo ainda está em uso em uma página e não pode ser removido");
        }

        storageClient.delete(arquivo.getChaveArquivo());
        if (arquivo.getThumbnailChave() != null) {
            storageClient.delete(arquivo.getThumbnailChave());
        }
        arquivoRepository.delete(arquivo);
    }

    @Transactional(readOnly = true)
    public Arquivo buscarParaDownload(UUID id) {
        return arquivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Arquivo não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ArquivoResponse> listarTodos() {
        return arquivoRepository.findAllByOrderByCriadoEmDesc().stream()
                .map(this::paraResponse)
                .toList();
    }

    private ArquivoResponse paraResponse(Arquivo arquivo) {
        String thumbnailUrl = arquivo.getThumbnailChave() != null
                ? storageClient.urlPublicaPara(arquivo.getThumbnailChave())
                : null;
        return ArquivoResponse.from(arquivo, thumbnailUrl);
    }
}

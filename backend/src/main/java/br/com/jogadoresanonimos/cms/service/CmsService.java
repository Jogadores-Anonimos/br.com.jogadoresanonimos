package br.com.jogadoresanonimos.cms.service;

import br.com.jogadoresanonimos.cms.dto.*;
import br.com.jogadoresanonimos.cms.entity.CmsAlteracaoLog;
import br.com.jogadoresanonimos.cms.entity.CmsBloco;
import br.com.jogadoresanonimos.cms.entity.CmsPagina;
import br.com.jogadoresanonimos.cms.entity.TipoBloco;
import br.com.jogadoresanonimos.cms.repository.CmsAlteracaoLogRepository;
import br.com.jogadoresanonimos.cms.repository.CmsPaginaRepository;
import br.com.jogadoresanonimos.cms.util.HtmlSanitizer;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.imagem.entity.Imagem;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CmsService {

    private final CmsPaginaRepository cmsPaginaRepository;
    private final CmsAlteracaoLogRepository cmsAlteracaoLogRepository;
    private final ImagemRepository imagemRepository;
    private final ArquivoRepository arquivoRepository;
    private final StorageClient storageClient;
    private final HtmlSanitizer htmlSanitizer;
    private final ObjectMapper objectMapper;

    public CmsService(CmsPaginaRepository cmsPaginaRepository, CmsAlteracaoLogRepository cmsAlteracaoLogRepository,
                       ImagemRepository imagemRepository, ArquivoRepository arquivoRepository, StorageClient storageClient,
                       HtmlSanitizer htmlSanitizer, ObjectMapper objectMapper) {
        this.cmsPaginaRepository = cmsPaginaRepository;
        this.cmsAlteracaoLogRepository = cmsAlteracaoLogRepository;
        this.imagemRepository = imagemRepository;
        this.arquivoRepository = arquivoRepository;
        this.storageClient = storageClient;
        this.htmlSanitizer = htmlSanitizer;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public CmsPaginaPublicaResponse buscarPublica(String slug) {
        CmsPagina pagina = cmsPaginaRepository.findBySlug(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.CMS_PAGINA_NAO_ENCONTRADA, "Página não encontrada"));
        return new CmsPaginaPublicaResponse(pagina.getSlug(), pagina.getTitulo(), montarBlocos(pagina));
    }

    @Transactional(readOnly = true)
    public List<CmsPaginaListItemResponse> listarAdmin() {
        return cmsPaginaRepository.findByOcultaNaListagemAdminFalse().stream()
                .map(p -> new CmsPaginaListItemResponse(p.getSlug(), p.getTitulo(), p.getAtualizadoEm(), p.getAtualizadoPor()))
                .toList();
    }

    @Transactional(readOnly = true)
    public CmsPaginaAdminResponse buscarAdmin(String slug) {
        CmsPagina pagina = cmsPaginaRepository.findBySlug(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.CMS_PAGINA_NAO_ENCONTRADA, "Página não encontrada"));
        return new CmsPaginaAdminResponse(pagina.getSlug(), pagina.getTitulo(), pagina.getAtualizadoEm(),
                pagina.getAtualizadoPor(), montarBlocos(pagina));
    }

    @Transactional
    public CmsPaginaAdminResponse criar(CmsPaginaCreateRequest request, UUID adminId) {
        if (cmsPaginaRepository.existsBySlug(request.slug())) {
            throw new ConflitoDeEstadoException(ErrorCode.CMS_SLUG_EM_USO, "Já existe uma página com este slug");
        }
        validarOrdens(request.blocos());
        CmsPagina pagina = new CmsPagina(request.slug(), request.titulo(), adminId);
        aplicarBlocos(pagina, request.blocos());
        pagina = cmsPaginaRepository.save(pagina);
        return buscarAdmin(pagina.getSlug());
    }

    @Transactional
    public CmsPaginaAdminResponse atualizar(String slug, CmsPaginaUpdateRequest request, UUID adminId) {
        CmsPagina pagina = cmsPaginaRepository.findBySlug(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.CMS_PAGINA_NAO_ENCONTRADA, "Página não encontrada"));
        validarOrdens(request.blocos());
        String snapshotAnterior = serializarSnapshot(pagina);
        cmsAlteracaoLogRepository.save(new CmsAlteracaoLog(pagina.getId(), adminId, snapshotAnterior));
        pagina.getBlocos().clear();
        cmsPaginaRepository.flush();
        aplicarBlocos(pagina, request.blocos());
        pagina.atualizar(adminId);
        return new CmsPaginaAdminResponse(pagina.getSlug(), pagina.getTitulo(), pagina.getAtualizadoEm(),
                pagina.getAtualizadoPor(), montarBlocos(pagina));
    }

    private void aplicarBlocos(CmsPagina pagina, List<BlocoRequest> blocosRequest) {
        for (BlocoRequest br : blocosRequest) {
            TipoBloco tipo = parseTipo(br.tipo());
            if (tipo == TipoBloco.texto) {
                String conteudoSanitizado = htmlSanitizer.sanitizar(br.conteudo());
                pagina.getBlocos().add(new CmsBloco(pagina, br.ordem(), tipo, conteudoSanitizado, null, null, null));
            } else if (tipo == TipoBloco.imagem) {
                if (br.imagemId() == null || !imagemRepository.existsById(br.imagemId())) {
                    throw new ValidationException(ErrorCode.CMS_BLOCO_REFERENCIA_INVALIDA, "imagemId não corresponde a um upload existente");
                }
                pagina.getBlocos().add(new CmsBloco(pagina, br.ordem(), tipo, null, br.imagemId(), null, br.alt()));
            } else {
                if (br.arquivoId() == null || !arquivoRepository.existsById(br.arquivoId())) {
                    throw new ValidationException(ErrorCode.CMS_BLOCO_REFERENCIA_INVALIDA, "arquivoId não corresponde a um upload existente");
                }
                pagina.getBlocos().add(new CmsBloco(pagina, br.ordem(), tipo, null, null, br.arquivoId(), null));
            }
        }
    }

    private void validarOrdens(List<BlocoRequest> blocos) {
        Set<Integer> vistas = new HashSet<>();
        for (BlocoRequest b : blocos) {
            if (!vistas.add(b.ordem())) {
                throw new ValidationException(ErrorCode.CMS_BLOCO_ORDEM_DUPLICADA, "Ordem de bloco duplicada",
                        null, Map.of("ordem", b.ordem()));
            }
        }
    }

    private TipoBloco parseTipo(String tipo) {
        try {
            return TipoBloco.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(ErrorCode.CMS_BLOCO_TIPO_INVALIDO, "Tipo de bloco inválido",
                    null, Map.of("tipo", tipo));
        }
    }

    private List<BlocoDto> montarBlocos(CmsPagina pagina) {
        List<CmsBloco> blocos = pagina.getBlocos();
        Set<UUID> imagemIds = blocos.stream()
                .filter(b -> b.getTipo() == TipoBloco.imagem)
                .map(CmsBloco::getImagemId)
                .collect(Collectors.toSet());
        Set<UUID> arquivoIds = blocos.stream()
                .filter(b -> b.getTipo() == TipoBloco.arquivo)
                .map(CmsBloco::getArquivoId)
                .collect(Collectors.toSet());
        Map<UUID, Imagem> imagensPorId = imagemIds.isEmpty() ? Map.of()
                : imagemRepository.findAllById(imagemIds).stream()
                        .collect(Collectors.toMap(Imagem::getId, Function.identity()));
        Map<UUID, Arquivo> arquivosPorId = arquivoIds.isEmpty() ? Map.of()
                : arquivoRepository.findAllById(arquivoIds).stream()
                        .collect(Collectors.toMap(Arquivo::getId, Function.identity()));
        return blocos.stream().map(b -> {
            if (b.getTipo() == TipoBloco.imagem) {
                Imagem imagem = imagensPorId.get(b.getImagemId());
                String url = imagem == null ? null : storageClient.urlPublicaPara(imagem.getChaveArquivo());
                return new BlocoDto(b.getOrdem(), b.getTipo().name(), null, b.getImagemId(), url, b.getAlt());
            }
            if (b.getTipo() == TipoBloco.arquivo) {
                Arquivo arquivo = arquivosPorId.get(b.getArquivoId());
                String url = arquivo == null ? null : "/api/v1/arquivos/" + arquivo.getId() + "/download";
                String titulo = arquivo == null ? null : arquivo.getTitulo();
                String descricao = arquivo == null ? null : arquivo.getDescricao();
                return BlocoDto.arquivo(b.getOrdem(), b.getArquivoId(), url, titulo, descricao);
            }
            return new BlocoDto(b.getOrdem(), b.getTipo().name(), b.getConteudo(), null, null, null);
        }).toList();
    }

    private String serializarSnapshot(CmsPagina pagina) {
        try {
            return objectMapper.writeValueAsString(montarBlocos(pagina));
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar snapshot de auditoria do CMS", e);
        }
    }
}

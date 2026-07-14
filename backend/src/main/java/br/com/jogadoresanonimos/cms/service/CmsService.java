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
import java.util.Set;
import java.util.UUID;

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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Página não encontrada"));
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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Página não encontrada"));
        return new CmsPaginaAdminResponse(pagina.getSlug(), pagina.getTitulo(), pagina.getAtualizadoEm(),
                pagina.getAtualizadoPor(), montarBlocos(pagina));
    }

    @Transactional
    public CmsPaginaAdminResponse criar(CmsPaginaCreateRequest request, UUID adminId) {
        if (cmsPaginaRepository.existsBySlug(request.slug())) {
            throw new ConflitoDeEstadoException("Já existe uma página com este slug");
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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Página não encontrada"));

        validarOrdens(request.blocos());

        String snapshotAnterior = serializarSnapshot(pagina);
        cmsAlteracaoLogRepository.save(new CmsAlteracaoLog(pagina.getId(), adminId, snapshotAnterior));

        // clear() agenda DELETE dos blocos antigos (orphanRemoval), mas
        // sem flush explícito o Hibernate pode ordenar os INSERTs dos
        // blocos novos (mesmo `ordem`, chave única `pagina_id+ordem`)
        // antes dos DELETEs no mesmo flush automático — violando a
        // constraint. Forçar o flush aqui garante que a exclusão dos
        // blocos antigos já aconteceu no banco antes de inserir os novos.
        pagina.getBlocos().clear();
        cmsPaginaRepository.flush();
        aplicarBlocos(pagina, request.blocos());
        pagina.atualizar(request.titulo(), adminId);

        return buscarAdmin(pagina.getSlug());
    }

    private void aplicarBlocos(CmsPagina pagina, List<BlocoRequest> blocosRequest) {
        for (BlocoRequest br : blocosRequest) {
            TipoBloco tipo = parseTipo(br.tipo());
            if (tipo == TipoBloco.texto) {
                String conteudoSanitizado = htmlSanitizer.sanitizar(br.conteudo());
                pagina.getBlocos().add(new CmsBloco(pagina, br.ordem(), tipo, conteudoSanitizado, null, null, null));
            } else if (tipo == TipoBloco.imagem) {
                if (br.imagemId() == null || !imagemRepository.existsById(br.imagemId())) {
                    throw new ValidationException("imagemId não corresponde a um upload existente");
                }
                pagina.getBlocos().add(new CmsBloco(pagina, br.ordem(), tipo, null, br.imagemId(), null, br.alt()));
            } else {
                if (br.arquivoId() == null || !arquivoRepository.existsById(br.arquivoId())) {
                    throw new ValidationException("arquivoId não corresponde a um upload existente");
                }
                pagina.getBlocos().add(new CmsBloco(pagina, br.ordem(), tipo, null, null, br.arquivoId(), null));
            }
        }
    }

    private void validarOrdens(List<BlocoRequest> blocos) {
        Set<Integer> vistas = new HashSet<>();
        for (BlocoRequest b : blocos) {
            if (!vistas.add(b.ordem())) {
                throw new ValidationException("Ordem de bloco duplicada: " + b.ordem());
            }
        }
    }

    private TipoBloco parseTipo(String tipo) {
        try {
            return TipoBloco.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Tipo de bloco inválido: " + tipo);
        }
    }

    private List<BlocoDto> montarBlocos(CmsPagina pagina) {
        return pagina.getBlocos().stream().map(b -> {
            if (b.getTipo() == TipoBloco.imagem) {
                String url = imagemRepository.findById(b.getImagemId())
                        .map(Imagem::getChaveArquivo)
                        .map(storageClient::urlPublicaPara)
                        .orElse(null);
                return new BlocoDto(b.getOrdem(), b.getTipo().name(), null, b.getImagemId(), url, b.getAlt());
            }
            if (b.getTipo() == TipoBloco.arquivo) {
                Arquivo arquivo = arquivoRepository.findById(b.getArquivoId()).orElse(null);
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

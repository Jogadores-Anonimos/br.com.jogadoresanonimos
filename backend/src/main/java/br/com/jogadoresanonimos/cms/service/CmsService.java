package br.com.jogadoresanonimos.cms.service;

import br.com.jogadoresanonimos.cms.dto.BlocoDto;
import br.com.jogadoresanonimos.cms.dto.CmsPaginaPublicaResponse;
import br.com.jogadoresanonimos.cms.entity.CmsBloco;
import br.com.jogadoresanonimos.cms.entity.CmsPagina;
import br.com.jogadoresanonimos.cms.entity.TipoBloco;
import br.com.jogadoresanonimos.cms.repository.CmsPaginaRepository;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.imagem.entity.Imagem;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CmsService {

    private final CmsPaginaRepository cmsPaginaRepository;
    private final ImagemRepository imagemRepository;
    private final ArquivoRepository arquivoRepository;
    private final StorageClient storageClient;

    public CmsService(CmsPaginaRepository cmsPaginaRepository, ImagemRepository imagemRepository,
                       ArquivoRepository arquivoRepository, StorageClient storageClient) {
        this.cmsPaginaRepository = cmsPaginaRepository;
        this.imagemRepository = imagemRepository;
        this.arquivoRepository = arquivoRepository;
        this.storageClient = storageClient;
    }

    @Transactional(readOnly = true)
    public CmsPaginaPublicaResponse buscarPublica(String slug) {
        CmsPagina pagina = cmsPaginaRepository.findBySlug(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.CMS_PAGINA_NAO_ENCONTRADA, "Página não encontrada"));
        return new CmsPaginaPublicaResponse(pagina.getSlug(), pagina.getTitulo(), montarBlocos(pagina));
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
}
